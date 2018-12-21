package org.androidannotations.internal;

import javax.annotation.processing.ProcessingEnvironment;

/**
 * @author lukas
 * @since 21.12.18
 */
public class PublicAndroidAnnotationsEnvironment extends InternalAndroidAnnotationsEnvironment {
    public PublicAndroidAnnotationsEnvironment(ProcessingEnvironment processingEnvironment) {
        super(processingEnvironment);
    }
}
