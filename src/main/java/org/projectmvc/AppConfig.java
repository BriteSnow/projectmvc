package org.projectmvc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import javax.inject.Inject;

import com.britesnow.snow.web.renderer.JsonRenderer;
import com.google.inject.matcher.AbstractMatcher;
import com.google.inject.matcher.Matcher;
import com.google.inject.matcher.Matchers;
import com.googlecode.gentyref.GenericTypeReflector;
import org.jomni.JomniMapper;
import org.projectmvc.access.DaoAccessInterceptor;
import org.projectmvc.access.annotation.AssertParamOrgPrivileges;
import org.projectmvc.access.annotation.AssertParamProjectPrivileges;
import org.projectmvc.access.annotation.AssertReturnOrgPrivileges;
import org.projectmvc.access.annotation.AssertReturnProjectPrivileges;
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
import com.google.inject.AbstractModule;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;

import static java.util.Arrays.asList;

/**
 *	<p>This is the default Guice Module for the application. By best practice, we prefix application config/binding classes with "App",
 *	such as <em>AppConfig</em> or <em>AppAuthService</em> which is bound to the snow <em>AuthService</em> interface.</p>
 *
 */
public class AppConfig extends AbstractModule {
	static private Logger log = LoggerFactory.getLogger(AppConfig.class);

	static private final Class<? extends Annotation>[] assertPrivilegeAnnotations = new Class[]{AssertParamOrgPrivileges.class, AssertReturnOrgPrivileges.class,
			AssertParamProjectPrivileges.class, AssertReturnProjectPrivileges.class};

	// --------- For DaoRegistry --------- //
	static private Matcher entityClassMatcher = Matchers.subclassesOf(BaseEntity.class).and(Matchers.not(matchAbstractClass()));

	static private Class[] entityClasses = new PackageScanner(BaseEntity.class.getPackage().getName())
															 .findClasses((c) -> entityClassMatcher.matches(c));
	// --------- /For DaoRegistry --------- //
	
	static public final String appVersion = "DROP-002-SNAPSHOT";

	@Override
	protected void configure() {
		// Bind the auth service implementation
		// IMPORTANT: We are binding by instance and requesting injection, because if AppAuthService instance Inject a dynamic "IDao<...>"
		// 			  object that are registered with requestInjection, the "bind(AuthRequest.class).to(AppAuthService.class)" would take precendence
		//            and cause missing bindings.
		// Note: If we were not using for example "IDao<Org,Long>" in AppAuthService and created a concrete "OrgDao" class, we would not
		//       need to bind to the instance and request injection, we should just do "bind(AuthRequest.class).to(AppAuthService.class)"
		bind(AuthRequest.class).to(AppAuthService.class);

		// bind the jsonRender
		bind(JsonRenderer.class).to(AppJsonRenderer.class);

		// --------- Performance Interceptor --------- //
		// bind the perf interceptor
		PerfInterceptor perfInterceptor = new PerfInterceptor();
		requestInjection(perfInterceptor);
		Matcher perfClassMatcher = Matchers.subclassesOf(BaseDao.class)
				.or(Matchers.annotatedWith(ToMonitor.class)).or(matchAnyOf(DaoHelper.class));
		bindInterceptor(perfClassMatcher, nonSyntheticMethodMatcher() , perfInterceptor);
		// --------- /Performance Interceptor --------- //

		// --------- Access Interceptor --------- //
		DaoAccessInterceptor daoAccessInterceptor = new DaoAccessInterceptor();
		requestInjection(daoAccessInterceptor);
		Matcher accessClassMatcher = Matchers.subclassesOf(BaseDao.class);
		Matcher accessMethodMatcher = annotatedWithAnyOf(assertPrivilegeAnnotations).and(nonSyntheticMethodMatcher());
		bindInterceptor(accessClassMatcher,accessMethodMatcher, daoAccessInterceptor);
		// --------- /Access Interceptor --------- //

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

	/**
	 * Return the Db JomniMapper for now.
	 */
	@Inject
	@Provides
	@Singleton
	public JomniMapper providesJomniMapper(DaoHelper daoHelper){
		return daoHelper.jomni;
	}

	// --------- For AOP Matching --------- //
	static private Matcher annotatedWithAnyOf(Class<? extends Annotation>... annotationTypes){
		Matcher m = Matchers.annotatedWith(annotationTypes[0]);
		if (annotationTypes.length > 1){
			for (Class<? extends Annotation> an : asList(annotationTypes).subList(1,annotationTypes.length)) {
				m = m.or(Matchers.annotatedWith(an));
			}
		}
		return m;
	}

	/**
	 * See: https://groups.google.com/forum/#!topic/google-guice/GqGJr2P99tU
	 *
	 * This allows to avoid intercepting the Synthetic method.
	 * @return
	 */
	static private Matcher nonSyntheticMethodMatcher(){
		Matcher m = new AbstractMatcher<Method>() {

			@Override
			public boolean matches(Method m) {
				return !m.isSynthetic();
			}
		};
		return m;
	}


	static private Matcher matchAnyOf(Class... classes) {
		Set<Class> classSet = new HashSet<>(asList(classes));
		return matchClass(c -> classSet.contains(c));
	}

	static private Matcher matchAbstractClass(){
		return matchClass(c -> Modifier.isAbstract(c.getModifiers()));
	}


	static private Matcher matchClass(Predicate<Class> predicate) {
		return new AbstractMatcher<Class>() {
			@Override
			public boolean matches(Class c) {
				return predicate.test(c);
			}
		};
	}
	// --------- /For AOP Matching --------- //


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
		try {
			bind(TypeLiteral.get(daoParamType)).toProvider((javax.inject.Provider) daoProvider);
		}catch (Throwable e){
			e.printStackTrace();
			throw new RuntimeException("AppConfig exception, cannot bind dao for " + entityClass + " daoProvider is null");
		}
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
