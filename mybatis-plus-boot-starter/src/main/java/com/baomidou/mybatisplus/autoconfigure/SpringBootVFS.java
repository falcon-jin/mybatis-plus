
package com.baomidou.mybatisplus.autoconfigure;


import com.baomidou.mybatisplus.core.toolkit.StringPool;
import org.apache.ibatis.io.VFS;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Hans Westerbeek
 * @author Eddú Meléndez
 * @author Kazuki Shimizu
 */
public class SpringBootVFS extends VFS {

    private final ResourcePatternResolver resourceResolver;

    public SpringBootVFS() {
        this.resourceResolver = new PathMatchingResourcePatternResolver(getClass().getClassLoader());
    }

    @Override
    public boolean isValid() {
        return true;
    }

    private static String preserveSubpackageName(final String baseUrlString, final Resource resource,
                                                 final String rootPath) {
        try {
            return rootPath + (rootPath.endsWith(StringPool.SLASH) ? StringPool.EMPTY : StringPool.SLASH)
                + resource.getURL().toString().substring(baseUrlString.length());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    protected List<String> list(URL url, String path) throws IOException {
        String urlString = url.toString();
        String baseUrlString = urlString.endsWith(StringPool.SLASH) ? urlString : urlString.concat(StringPool.SLASH);
        Resource[] resources = resourceResolver.getResources(baseUrlString + "**/*.class");
        return Stream.of(resources).map(resource -> preserveSubpackageName(baseUrlString, resource, path))
            .collect(Collectors.toList());
    }
}
