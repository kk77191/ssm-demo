package com.lab.cms.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.regex.Pattern;

public class XssFilter implements Filter {

    private static final Pattern[] XSS_PATTERNS = {
            Pattern.compile("<script[^>]*>.*?</script>", Pattern.CASE_INSENSITIVE | Pattern.DOTALL),
            Pattern.compile("</?script[^>]*>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("javascript\\s*:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("vbscript\\s*:", Pattern.CASE_INSENSITIVE),
            Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<\\s*(iframe|object|embed|form|input|body|img)[^>]*>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<\\s*/\\s*(iframe|object|embed|form|input|body|img)\\s*>", Pattern.CASE_INSENSITIVE),
            Pattern.compile("expression\\s*\\(", Pattern.CASE_INSENSITIVE),
            Pattern.compile("url\\s*\\(\\s*javascript:", Pattern.CASE_INSENSITIVE)
    };

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        chain.doFilter(new XssRequestWrapper(request), res);
    }

    @Override
    public void destroy() {}

    public static String stripXss(String value) {
        if (value == null || value.isEmpty()) return value;
        String cleaned = value;
        for (Pattern p : XSS_PATTERNS) {
            cleaned = p.matcher(cleaned).replaceAll("");
        }
        cleaned = cleaned.replace("<", "&#60;").replace(">", "&#62;")
                .replace("\"", "&quot;").replace("'", "&#39;");
        return cleaned;
    }

    static class XssRequestWrapper extends HttpServletRequestWrapper {
        public XssRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return value == null ? null : stripXss(value);
        }

        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) return null;
            String[] sanitized = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                sanitized[i] = values[i] == null ? null : stripXss(values[i]);
            }
            return sanitized;
        }

        @Override
        public String getHeader(String name) {
            String value = super.getHeader(name);
            return value == null ? null : stripXss(value);
        }
    }
}