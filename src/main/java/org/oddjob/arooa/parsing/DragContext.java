package org.oddjob.arooa.parsing;

import org.oddjob.arooa.*;
import org.oddjob.arooa.json.JsonConfiguration;
import org.oddjob.arooa.life.InstantiationContext;
import org.oddjob.arooa.life.SimpleArooaClass;
import org.oddjob.arooa.registry.ChangeHow;
import org.oddjob.arooa.xml.XMLConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Pattern;

/**
 * A {@link DragPoint} for an {@link ArooaContext}.
 * <p>
 * This implementation of a DragPoint will provide drag and drop support
 * for a component from it's context.
 * 
 * @author rob
 *
 */
public class DragContext implements DragPoint {
	private static final Logger logger = LoggerFactory.getLogger(DragContext.class);

	private static final Pattern XML_START_MATCH = Pattern.compile("^\\s*<");
	private static final Pattern JSON_START_MATCH = Pattern.compile("^\\s*\\{");

	private static SimpleTransaction transaction;

	private final ArooaContext context;
	
	private final CutAndPasteSupport cnp;

	/**
	 * Only constructor.
	 * 
	 * @param context The context of the component. Must not be null.
	 */
	public DragContext(ArooaContext context) {
		this.context = context;
		this.cnp = new CutAndPasteSupport(context);
	}

	@Override
	public DragTransaction beginChange(ChangeHow how) {
		return createTransaction(how);
	}

	@Override
	public <P extends ParseContext<P>> ConfigurationHandle<P> parse(P parentContext)
			throws ArooaParseException {
		return context.getConfigurationNode().parse(parentContext);
	}

	@Override
	public boolean supportsPaste() {
		return cnp.supportsPaste();
	}

	@Override
	public boolean supportsCut() {
		return context.getParent().getParent() != null;
	}

	@Override
	public String copy() {
		return CutAndPasteSupport.copy(context);
	}
	
	private void reallyCut() {
		CutAndPasteSupport.cut(
				context.getParent(), context);
	}
		
	private void reallyPaste(int index, ArooaConfiguration pasteConfig)
	throws ArooaParseException {

		cnp.paste(index, pasteConfig);
	}

	@Override
	public String cut() {
		String copy = CutAndPasteSupport.copy(context);
		delete();
		return copy;
	}

	@Override
	public void delete() {
		synchronized(DragContext.class) {
			if (transaction == null) {
				throw new IllegalStateException("No transaction");
			}
			transaction.setCut(this);
		}
	}

	@Override
	public void paste(int index, String config) {
		if (!cnp.supportsPaste()) {
			throw new IllegalStateException("Node does not support paste.");
		}

		ArooaConfiguration pasteConfig;
		if (XML_START_MATCH.matcher(config).find()) {
			pasteConfig = new XMLConfiguration("Replacement XML", config);
		}
		else if (JSON_START_MATCH.matcher(config).find()) {
			pasteConfig = new JsonConfiguration(config)
					.withNamespaceMappings(context.getSession().getArooaDescriptor());
		}
		else {
			throw new IllegalArgumentException(
					"Expected config to start with '<' (XML) or '{' (JSON) but was [" +
							config + "]");
		}

		synchronized(DragContext.class) {
			if (transaction == null) {
				throw new IllegalStateException("No transaction");
			}
			transaction.setPaste(this, index, pasteConfig);
		}
	}

	@Override
	public QTag[] possibleChildren() {
		ArooaDescriptor descriptor = context.getSession().getArooaDescriptor();

		InstantiationContext context = new InstantiationContext(
				ArooaType.COMPONENT, new SimpleArooaClass(Object.class));

		ArooaElement[] elements = descriptor.getElementMappings().elementsFor(
				context);

		SortedSet<QTag> sortedTags = new TreeSet<>();

		for (ArooaElement element : elements) {
			String prefix = descriptor.getPrefixFor(element.getUri());
			sortedTags.add(new QTag(prefix, element));
		}

		return sortedTags.toArray(new QTag[0]);
	}

	private DragTransaction createTransaction(ChangeHow how) {
		
		synchronized (DragContext.class) {
			switch (how) {
				case AGAIN:
					if (transaction == null) {
						throw new IllegalStateException("There is no transaction in progress.");
					}
				case MAYBE:
					if (transaction == null) {
						return null;
					}
					if (transaction.isNotTransactionThread()) {
						throw new IllegalStateException(
								"Transaction is on a different thread.",
								transaction.getTransactionCreation());
					}
					return transaction;
				case EITHER:
					if (transaction != null) {
						if (transaction.isNotTransactionThread()) {
							throw new IllegalStateException(
									"Transaction is on a different thread.",
									transaction.getTransactionCreation());
						}
						return new SimpleTransaction() {
							public void commit() {
							}
							public void rollback() {
								transaction.rollbackOnly = true;
							}
						};
					}
				case FRESH:
					if (transaction != null) {
						throw new IllegalStateException(
								"There is already a transaction in progress.",
								transaction.getTransactionCreation());
					}
					transaction = new SimpleTransaction();
					return transaction;
				default:
					throw new IllegalArgumentException("Unrecognized how.");
			}
		}
	}
			
	static class SimpleTransaction implements DragTransaction {
		
		private final Exception transactionCreation;
		
		private DragContext cutPoint;
		
		private int pasteIndex;

		private ArooaConfiguration pasteConfig;
		
		private DragContext pastePoint;
		
		private final Thread transactionThread;
		
		private boolean rollbackOnly;
		
	    private final Object transactionLock = new Object();
	    
		private int cutIndex;
		private ArooaConfiguration cutConfiguration = null;
	    
		public SimpleTransaction() {
			transactionCreation = new Exception("Transaction Creation Point.");
			transactionThread = Thread.currentThread();
		}
		
		Exception getTransactionCreation() {
			return transactionCreation;
		}
		
		boolean isNotTransactionThread() {
			return Thread.currentThread() != transactionThread;
		}
		
		public void commit() throws ArooaParseException {
			synchronized (transactionLock) {
				if (transaction == null) {
					throw new IllegalStateException("Transaction already complete.");
				}
				
				if (rollbackOnly) {
					rollback();
					return;
				}

				
				if (cutPoint != null) {
					if (pastePoint != null) {
						ArooaContext parentContext = 
							cutPoint.context.getParent();
						
						cutIndex = parentContext.getConfigurationNode().indexOf(
								cutPoint.context.getConfigurationNode());
						
						cutConfiguration = cutPoint.context.getConfigurationNode();
					}
					
					cutPoint.reallyCut();
				}
				
				if (pastePoint != null) {
					pastePoint.reallyPaste(pasteIndex, pasteConfig);
				}
				reset();
			}
		}
		
		public void rollback() {
			synchronized (transactionLock) {
				if (transaction == null) {
					throw new IllegalStateException("Transaction already complete.");
				}
				try {
					if (cutConfiguration != null) {
						try {
							CutAndPasteSupport.paste(
									cutPoint.context.getParent(),
									cutIndex,
									cutConfiguration);
						} 
						catch (ArooaParseException e) {
							logger.error("Failed replacing cut configuration.",
									e);
						}
					}
				}
				finally {
					reset();
				}
			}
		}
		
		private void reset() {
			cutPoint = null;
			pastePoint = null;
			pasteIndex = 0;
			pasteConfig = null;
			transaction = null;
			cutConfiguration = null;
			cutIndex = 0;
		}
		
		void setPaste(DragContext point, int index, ArooaConfiguration config) {
			
			synchronized (transactionLock) {
				if (pastePoint != null) {
					throw new IllegalStateException("Only one paste supported.");
				}
				pastePoint  = point;
				pasteIndex = index;
				pasteConfig = config;
			}
		}
		
		public void setCut(DragContext point) {
			
			synchronized (transactionLock) {
				if (cutPoint != null) {
					throw new IllegalStateException("Only one paste supported.");
				}
				cutPoint = point;
			}
		}
	}
}
