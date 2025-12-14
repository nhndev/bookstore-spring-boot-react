package com.nhn.util;

import java.io.StringWriter;
import java.util.Properties;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class VelocityUtil {
    public static String generate(final String vmFolder,
                                  final String vmFileName,
                                  final VelocityContext context) {
        try {
            final Properties properties = new Properties();
            properties.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH,
                                   vmFolder);
            Velocity.init(properties);

            final StringWriter stringWriter = new StringWriter();
            Velocity.mergeTemplate(vmFileName, "utf-8", context, stringWriter);
            return stringWriter.toString();
        } catch (final Exception ex) {
            log.error("VelocityUtil.generate error", ex);
            throw ex;
        }
    }
}
