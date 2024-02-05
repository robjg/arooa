package org.oddjob.arooa.beandocs;

import java.util.Map;
import java.util.TreeMap;

public class WriteableArooaDoc implements ArooaDoc {

	private final Map<Tag, WriteableBeanDoc> beanDocs = new TreeMap<>();
	
	@Override
	public WriteableBeanDoc beanDocFor(String prefix, String tag) {
		return beanDocs.get(new Tag(prefix, tag));
	}
	
	@Override
	public WriteableBeanDoc[] getBeanDocs() {
		return beanDocs.values().toArray(new WriteableBeanDoc[0]);
	}
	
	public void add(WriteableBeanDoc beanDocBean) {
		beanDocs.put(new Tag(beanDocBean.getPrefix(), beanDocBean.getTag()), 
				beanDocBean);
	}	
	
	static class Tag implements Comparable<Tag> {
		
		private final String prefix;
		private final String tag;
		
		public Tag(String prefix, String tag) {
			this.prefix = prefix == null ? "" : prefix;
			this.tag = tag;
		}		
		
		public String getPrefix() {
			return prefix;
		}
		
		public String getTag() {
			return tag;
		}
				
		@Override
		public boolean equals(Object obj) {
			if (obj == null) {
				return false;
			}
			if (obj == this) {
				return true;
			}
			
			if (! (obj instanceof Tag)) {
				return false;
			}
			
			Tag other = (Tag) obj;

            return other.prefix.equals(prefix)
                    && other.tag.equals(tag);
		}
		
		@Override
		public int hashCode() {
			return tag.hashCode() + prefix.hashCode();
		}
		
		public int compareTo(Tag other) {
			int prefixCompare = prefix.compareTo(other.prefix); 
			if ( prefixCompare != 0) {
				return prefixCompare;
			}
			return tag.compareTo(other.tag);
		}
		
		@Override
		public String toString() {
			if (prefix.isEmpty()) {
				return tag;
			}
			return prefix + ":" + tag;
		}
	}
}
