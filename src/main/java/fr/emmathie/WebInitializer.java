package fr.emmathie;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebInitializer {

    public class MyWebInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

        @Override
        protected Class<?>[] getServletConfigClasses() {
            return new Class[] {DispatcherConfig.class};
        }

        @Override
        protected String[] getServletMappings() {
            return new String[] {"/"};
        }

        @Override
        protected Class<?>[] getRootConfigClasses() {
            return null;
        }

    }

}
