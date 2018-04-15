package com.kardach.kweb.rest.endpoint;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;

import com.kardach.kweb.annotation.Delete;
import com.kardach.kweb.annotation.Get;
import com.kardach.kweb.annotation.Post;
import com.kardach.kweb.annotation.Put;
import com.kardach.kweb.annotation.Rest;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EndpointsGenerator {

	public static Map<String, EndpointNode> endpoints = new HashMap<>();

	private static final List<Class<?>> restMethods = Arrays.asList(Get.class, Post.class, Put.class, Delete.class);

	public static void scan(Class<?> root) {
		Set<Class<?>> rests = getRests(root);

		for (Class<?> controller : rests) {
			log.debug("Controller: [{}]", controller.getName());
			Rest restAnnotation = controller.getAnnotation(Rest.class);
			String prefixUrl = restAnnotation.url();
			Method[] methods = controller.getDeclaredMethods();
			Arrays.stream(methods).forEach(i -> EndpointsGenerator.addRestMethodToEndpoints(prefixUrl, i));
		}
	}

	/**
	 * Takes classes with {@link Rest} annotation by reflection API
	 * 
	 * @return
	 */
	private static Set<Class<?>> getRests(Class<?> root) {
		Package rootPackage = root.getPackage();
		log.info("scan package={}", rootPackage);

		/* my bad solution #1 */
		// ClassFinder.find(rootPackage.getName()).stream().forEach(i -> log.info("ann:
		// {}", i.getName()));

		/* my bad solution #2 */
		/*
		 * try{ return ReflectionUtil.getClasses(rootPackage.getName(), Rest.class);
		 * }catch(Exception e) { log.info("Can't get classes from package: {}",
		 * rootPackage.getName()); return new HashSet<>(); }
		 */

		Reflections reflections = new Reflections(rootPackage.getName());
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Rest.class);
		annotated.stream().forEach(i -> log.info("annotated: {}", i.getName()));
		return annotated;
	}

	private static void addRestMethodToEndpoints(String prefixUrl, final Method method) {
		final Annotation restAnnotation = getRestAnnotation(method);
		if (restAnnotation == null) {
			return;
		}
		
		final Class<?> annotationType = restAnnotation.annotationType();
		final String fullRestVerb = annotationType.getName();

		EndpointNode node = endpoints.get(fullRestVerb);
		if (node == null) {
			node = new EndpointNode(null);
			endpoints.put(fullRestVerb, node);
		}
		
		String value = getRestValue(method, restAnnotation);
		String url = prefixUrl + value;
		String[] pathParts = getPathParts(url);
		addEndpointNode(node, 0, pathParts);

		log.info("{} {} [method: {}]", annotationType.getSimpleName().toUpperCase(), url, method.getName());
	}

	/**
	 * Added endpoint to tree as a {@link EndpointNode} elements.
	 * 
	 * @param parrentNode
	 * @param iPath
	 * @param path
	 */
	private static void addEndpointNode(EndpointNode parrentNode, int iPath, String... path) {
		if (path == null || iPath >= path.length) {
			return;
		}
		final String pathElement = path[iPath];
		EndpointNode node = new EndpointNode(pathElement);
		String key = (node.isPath()) ? "{}" : pathElement;
		
		Map<String, EndpointNode> nextMap = parrentNode.getNext();
		EndpointNode nextNode = nextMap.get(key);
		if (nextNode == null) {
			nextNode = node;
			nextMap.put(key, nextNode);
		}
		addEndpointNode(nextNode, iPath + 1, path);
	}
	
	private static String getRestValue(Method method, Annotation annotation) {
		Class<?> annotationType = annotation.annotationType();
		if (annotationType.equals(Get.class)) {
			return method.getAnnotation(Get.class).value();
		} else if (annotationType.equals(Post.class)) {
			return method.getAnnotation(Post.class).value();
		} else if (annotationType.equals(Put.class)) {
			return method.getAnnotation(Put.class).value();
		} else if (annotationType.equals(Delete.class)) {
			return method.getAnnotation(Delete.class).value();
		}
		throw new RuntimeException("no such annotation");
	}

	private static String[] getPathParts(String value) {
		if (value.startsWith("/")) {
			value = value.substring(1);
		}
		String[] result = value.split("/");
		return result;
	}

	private static Annotation getRestAnnotation(final Method method) {
		Set<Annotation> result = getAllRestAnnotations(method);
		if (result == null) {
			return null;
		}
		if (result.size() != 1) {
			throw new RuntimeException("Should be only one Restful annotation for");
		}
		return result.iterator().next();
	}

	private static Set<Annotation> getAllRestAnnotations(final Method method) {
		Annotation[] annotations = method.getDeclaredAnnotations();
		return Arrays.stream(annotations).filter(ann -> restMethods.contains(ann.annotationType()))
				.collect(Collectors.toSet());
	}
}
