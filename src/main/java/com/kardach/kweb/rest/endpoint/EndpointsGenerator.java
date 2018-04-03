package com.kardach.kweb.rest.endpoint;

import com.kardach.kweb.annotation.Delete;
import com.kardach.kweb.annotation.Get;
import com.kardach.kweb.annotation.Post;
import com.kardach.kweb.annotation.Put;
import com.kardach.kweb.annotation.Rest;
import com.kardach.kweb.rest.ClassFinder;
import com.kardach.kweb.server.HttpRequestMethod;
import com.kardach.kweb.rest.endpoint.EndpointNode;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.reflections.*;

@Slf4j
public class EndpointsGenerator {

	public static Map<String, EndpointNode> endpoints = new HashMap<>();

	private static final List<Class<?>> restMethods = Arrays.asList(
		Get.class,
		Post.class,
		Put.class,
		Delete.class
	);

	public static void scan(Class<?> root) {
		Package rootPackage = root.getPackage();
		log.info("scan package={}", rootPackage);
		List<Class<?>> classes = null;
		Reflections reflections = new Reflections(rootPackage.getName());
		Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Rest.class);
		annotated.stream().forEach(i -> log.info("annotated: {}", i.getName()));

		try{
			//classes = ClassFinder.find(rootPackage.getName());
			classes = getClasses(rootPackage.getName());
		}catch(Exception e) {
			log.info("Can't get classes from package: {}", rootPackage.getName());
			return;
		}
		log.info("There are {} in package={}", classes, rootPackage.getName());
		Set<Class<?>> rests = classes.stream().filter(i -> i.isAnnotationPresent(Rest.class)).collect(Collectors.toSet());
		log.info("Rest Controllers: {}", rests);
		for(Class<?> controller : rests) {
			Method[] methods = controller.getDeclaredMethods();
			Arrays.stream(methods)
				.filter(EndpointsGenerator::isRestable)
				.forEach(EndpointsGenerator::addRestMethodToEndpoints);
		}
	}

	/**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private static List<Class<?>> getClasses(String packageName)
            throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        List<Class<?>> classes = new ArrayList<>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }

	private static boolean isRestable(final Method method) {
		Annotation[] annotations = method.getDeclaredAnnotations();
		int count = 0;
		for(Annotation an : annotations) {
			if(restMethods.contains(an.getClass())) {
				count++;
			}
		}
		if(count > 1) {
			log.error("Should be only one Restful annotation for {}", method.getName());
		}
		return count == 1;
	}

	private static void addRestMethodToEndpoints(final Method method) {
		final Annotation restAnnotation = getRestAnnotationForMethod(method);
		final String restVerb = restAnnotation.getClass().getName();
		EndpointNode node = endpoints.get(restVerb);
		if(node == null) {
			node = new EndpointNode();
		}
		if(node.getNext().contains(restVerb)) {

		}
		log.info("Rest method: {}", method.getName());
	}

	private static Annotation getRestAnnotationForMethod(final Method method) {
		Annotation[] annotations = method.getDeclaredAnnotations();
		return Arrays.stream(annotations)
				.filter(ann -> restMethods.contains(ann.getClass()))
				.findFirst()
				.get();
	}

}
