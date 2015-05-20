package org.projectmvc;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import com.britesnow.snow.web.renderer.JsonRenderer;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.googlecode.gentyref.GenericTypeReflector;
import org.projectmvc.dao.BaseDao;
import org.projectmvc.dao.DaoHelper;
import org.projectmvc.dao.DaoRegistry;
import org.projectmvc.dao.IDao;
import org.projectmvc.entity.BaseEntity;
import org.projectmvc.perf.PerfInterceptor;
import org.projectmvc.perf.annotation.ToMonitor;
import org.projectmvc.web.AppAuthService;
import org.projectmvc.web.AppJsonRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.britesnow.snow.util.PackageScanner;
import com.britesnow.snow.web.auth.AuthRequest;
import com.britesnow.snow.web.binding.EntityClasses;
import com.google.common.base.Predicate;
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

/**
 *	<p>This is the default Guice Module for the application. By best practice, we prefix application config/binding classes with "App",
 *	such as <em>AppConfig</em> or <em>AppAuthService</em> which is bound to the snow <em>AuthService</em> interface.</p>
 *
 */
public class AppConfig extends AbstractModule {
	private static Logger log = LoggerFactory.getLogger(AppConfig.class);
	
	// --------- For DaoRegistry --------- //
	// TODO: needs to just check if class is abstract
	// NOTE: (Class)cls cast below as a workaround to IntelliJ editor bug that does not yet recognized the method's generic. 
	private static Class[] entityClasses = new PackageScanner(BaseEntity.class.getPackage().getName())
															 .findClasses((cls) -> (cls != BaseEntity.class && BaseEntity.class.isAssignableFrom((Class) cls)));
	// --------- /For DaoRegistry --------- //
	
	@Override
	protected void configure() {
		// bind the auth service implementation
		bind(AuthRequest.class).to(AppAuthService.class);

		// bind the jsonRender
		bind(JsonRenderer.class).to(AppJsonRenderer.class);

		// --------- For Performance Instrumatization --------- //
		// bind the perf interceptor
		PerfInterceptor perfInterceptor = new PerfInterceptor();
		requestInjection(perfInterceptor);
		bindInterceptor(perfClassMatchers(), perfMethodMatchers() , perfInterceptor);
		// --------- /For Performance Instrumatization --------- //

		// --------- For DaoRegistry --------- //
		// Find and bind the dao for each Entity class (and create a genericDao instance if none defined).
		// Note: This is not very "pure Guice" but it provides a great flexibility as it allows to have IDao<EntityClass>
		//       pattern regardless if there is a specific dao define for this entity (either the concrete dao will be taken
		//       or a instance of the GenericDao will be created if not found).
		//       In other word, a little hack for lot of elegancy.
		for (Class entityClass : entityClasses){
			bindDao(entityClass);
		}
		// --------- /For DaoRegistry --------- //
	}

	// --------- For Performance Instrumatization --------- //
	private Matcher perfClassMatchers(){
		Matcher m = Matchers.subclassesOf(BaseDao.class);
		m = m.or(Matchers.annotatedWith(ToMonitor.class));
		m = m.or(new ClassSetMatcher(DaoHelper.class));
		return m;
	}

	/**
	 * See: https://groups.google.com/forum/#!topic/google-guice/GqGJr2P99tU
	 *
	 * This allows to avoid intercepting the Synthetic method.
	 * @return
	 */
	private Matcher perfMethodMatchers(){
		Matcher m = new AbstractMatcher<Method>() {
			@Override
			public boolean matches(Method m) {
				return !m.isSynthetic();
			}
		};
		return m;
	}

	class ClassSetMatcher extends AbstractMatcher<Class>{
		Set<Class> classSet = new HashSet<Class>();

		ClassSetMatcher(Class... classes){
			for (Class cls : classes) {
				classSet.add(cls);
			}
		}

		@Override
		public boolean matches(Class c) {
			return classSet.contains(c);
		}
	}	
	// --------- /For Performance Instrumatization --------- //

	// --------- For DaoRegistry --------- //
	// Just return the static entityClasses value, allowing @EntityClasses to be injected.
	@Provides
	@Singleton
	@EntityClasses
	public Class[] provideEntityClasses() {
			return entityClasses;
	}
	
	
	private <T> void bindDao(final Class entityClass){
		final Type idClass = GenericTypeReflector.getTypeParameter(entityClass, BaseEntity.class.getTypeParameters()[0]);

		Type daoParamType = new ParameterizedType() {
			public Type getRawType() {
				return IDao.class;
			}

			public Type getOwnerType() {
				return null;
			}

			public Type[] getActualTypeArguments() {
				return new Type[] {entityClass,idClass};
			}
		};        
		
		DaoProvider daoProvider = new DaoProvider(entityClass);
		requestInjection(daoProvider);
		bind(TypeLiteral.get(daoParamType)).toProvider((javax.inject.Provider)daoProvider);
	}
	// --------- /For DaoRegistry --------- //
	
}

// --------- For DaoRegistry --------- //
class DaoProvider implements Provider{
	
	private Class entityClass;
	
	@Inject
	private DaoRegistry daoRegistry;

	public DaoProvider(Class entityClass){
		this.entityClass = entityClass;
	}

	@Override
	public Object get() {
		IDao dao = daoRegistry.getDao(entityClass);
		return dao;
	}
}
// --------- /For DaoRegistry --------- //
