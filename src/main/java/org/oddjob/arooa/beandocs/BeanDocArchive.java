package org.oddjob.arooa.beandocs;

import java.util.List;

/**
 *  Something that is able to provide {@link BeanDoc}s for jobs and types. This
 *  is currently only use in oj-docs but is here as its intended that an implementation
 *  could read a local archive of docs and provide them interactively in the Oddjob
 *  Designer.
 */
public interface BeanDocArchive {

    List<BeanDoc> allJobDoc();

    List<BeanDoc> allTypeDoc();

}
