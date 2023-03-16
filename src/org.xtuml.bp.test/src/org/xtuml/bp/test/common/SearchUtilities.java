package org.xtuml.bp.test.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.search.internal.ui.SearchDialog;
import org.eclipse.search.ui.IQueryListener;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.WorkbenchPart;
import org.xtuml.bp.core.ModelClass_c;
import org.xtuml.bp.core.Ooaofooa;
import org.xtuml.bp.core.SearchResult_c;
import org.xtuml.bp.core.common.NonRootModelElement;
import org.xtuml.bp.core.util.EditorUtil;
import org.xtuml.bp.search.results.ModelSearchResult;
import org.xtuml.bp.test.TestUtil;
import org.xtuml.bp.ui.search.pages.ModelSearchResultPage;

@SuppressWarnings("restriction")
public class SearchUtilities {

	public enum ResultType { NAME, KEYLETTERS };
	private volatile static boolean complete = false;
	public static void configureAndRunSearch(final String pattern,
			final boolean regEx, final boolean caseSensitive,
			final boolean oal, final boolean descriptions, final boolean names, final int scope,
			final String workingSet) {
		// wait for search to complete
		NewSearchUI.addQueryListener(new IQueryListener() {
			@Override
			public void queryStarting(ISearchQuery query) {
			}
			@Override
			public void queryRemoved(ISearchQuery query) {
			}
			@Override
			public void queryFinished(ISearchQuery query) {
				complete = true;
			}
			@Override
			public void queryAdded(ISearchQuery query) {
			}
		});
		BaseTest.dispatchEvents();
		// clear previous results
		SearchResult_c[] results = SearchResult_c.SearchResultInstances(
				Ooaofooa.getDefaultInstance(), null, false);
		for (int i = 0; i < results.length; i++) {
			results[i].Dispose();
		}
		TestUtil.processShell(null, shell -> {
			Object data = shell.getData();
			Control[] children = shell.getChildren();
			Combo combo = getPatternField((Composite) children[0]);
			Button[] buttons = getConfigurationButtons((Composite) children[0]);
			Button[] scopeButtons = getScopeButtons((Composite) children[0]);
			Button search = getButton((Composite) children[0], "&Search");
			combo.setText(pattern);
			for (int i = 0; i < buttons.length; i++) {
				if (buttons[i].getText().equals("Regular expression")) {
					buttons[i].setSelection(regEx);
					buttons[i].notifyListeners(SWT.Selection, new Event());
				} else if (buttons[i].getText().equals("Case sensitive")) {
					buttons[i].setSelection(caseSensitive);
					buttons[i].notifyListeners(SWT.Selection, new Event());
				} else if (buttons[i].getText().equals("Action Language")) {
					buttons[i].setSelection(oal);
					buttons[i].notifyListeners(SWT.Selection, new Event());
				} else if (buttons[i].getText().equals("Descriptions")) {
					buttons[i].setSelection(descriptions);
					buttons[i].notifyListeners(SWT.Selection, new Event());
				} else if (buttons[i].getText().equals("Element Names")) {
					buttons[i].setSelection(names);
					buttons[i].notifyListeners(SWT.Selection, new Event());
				}
			}
			for (int i = 0; i < scopeButtons.length; i++) {
				if (scopeButtons[i].getText().startsWith("&Workspace") && scope == ISearchPageContainer.WORKSPACE_SCOPE) {
					scopeButtons[i].setSelection(true);
					scopeButtons[i].notifyListeners(SWT.Selection, new Event());
					break;
				}
				if (scopeButtons[i].getText().startsWith("Selecte&d resource")
						&& scope == ISearchPageContainer.SELECTION_SCOPE) {
					scopeButtons[i].setSelection(true);
					scopeButtons[i].notifyListeners(SWT.Selection, new Event());
					BaseTest.dispatchEvents();
					break;
				}
				if (scopeButtons[i].getText().startsWith("Enclosing pro&ject")
						&& scope == ISearchPageContainer.SELECTED_PROJECTS_SCOPE) {
					scopeButtons[i].setSelection(true);
					scopeButtons[i].notifyListeners(SWT.Selection, new Event());
					BaseTest.dispatchEvents();
					break;
				}
				if (scopeButtons[i].getText().startsWith("Wor&king set:")
						&& scope == ISearchPageContainer.WORKING_SET_SCOPE) {
					scopeButtons[i].setSelection(true);
					scopeButtons[i].notifyListeners(SWT.Selection, new Event());
					((SearchDialog) data).setSelectedWorkingSets(new IWorkingSet[] {
							PlatformUI.getWorkbench().getWorkingSetManager().getWorkingSet(workingSet) });
					BaseTest.dispatchEvents();
					break;
				}
			}
			search.notifyListeners(SWT.Selection, new Event());
			BaseTest.dispatchEvents();
			return true;
		});
		NewSearchUI.openSearchDialog(PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow(),
				"org.xtuml.bp.ui.search.xtumlSearchPage");
		// wait for search to complete
		ISearchQuery[] queries = NewSearchUI.getQueries();
		for(int i = 0; i < queries.length; i++) {
			while(NewSearchUI.isQueryRunning(queries[i])) {
				BaseTest.dispatchEvents();
			}
		}
		
		while(!complete)
			BaseTest.dispatchEvents();
	}

	protected static Button getButton(Composite composite, String name) {
		Control[] children = composite.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Composite) {
				Button button = getButton((Composite) children[i], name);
				if(button != null) {
					return button;
				}
			} else if (children[i] instanceof Button) {
				if (((Button) children[i]).getText().equals(name) || ((Button) children[i]).getText().equals(name.replaceAll("&", ""))) {
					return (Button) children[i];
				}
			}
		}
		return null;
	}

	protected static Button[] getScopeButtons(Composite composite) {
		List<Button> buttons = new ArrayList<Button>();
		Control[] children = composite.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Group) {
				if (((Group) children[i]).getText().equals("Scope")) {
					Control[] scopeChildren = ((Group) children[i])
							.getChildren();
					for (int j = 0; j < scopeChildren.length; j++) {
						if (scopeChildren[j] instanceof Button
								&& !((Button) scopeChildren[j]).getText()
										.equals("C&hoose...")) {
							buttons.add((Button) scopeChildren[j]);
						}
					}
				}
			} else if (children[i] instanceof Composite) {
				buttons.addAll(Arrays
						.asList(getScopeButtons((Composite) children[i])));
			}
		}
		return buttons.toArray(new Button[buttons.size()]);
	}

	protected static Button[] getConfigurationButtons(Composite composite) {
		List<Button> buttons = new ArrayList<Button>();
		Control[] children = composite.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Group) {
				Group group = (Group) children[i];
				if (group.getText().equals("Limit To")) {
					Control[] groupChildren = group.getChildren();
					for (int j = 0; j < groupChildren.length; j++) {
						if (groupChildren[j] instanceof Button) {
							buttons.add((Button) groupChildren[j]);
						}
					}
				}				
			} else if (children[i] instanceof Composite) {
				buttons
						.addAll(Arrays
								.asList(getConfigurationButtons((Composite) children[i])));
			} else if (children[i] instanceof Button) {
				if (((Button) children[i]).getText().equals("Case sensitive")
						|| ((Button) children[i]).getText().equals(
								"Regular expression")) {
					buttons.add((Button) children[i]);
				}
			}
		}
		return buttons.toArray(new Button[buttons.size()]);
	}

	protected static Combo getPatternField(Composite composite) {
		Control[] children = composite.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (children[i] instanceof Combo) {
				return (Combo) children[i];
			} else if (children[i] instanceof Composite) {
				Combo patternCombo = getPatternField((Composite) children[i]);
				if (patternCombo != null) {
					return patternCombo;
				}
			}
		}
		return null;
	}

	public static Object findResultInView(String itemName) {
		return findResultInView(itemName, ResultType.NAME);
	}

	public static Object findResultInView(String itemName, ResultType rt) {
		SearchResult_c[] searchResults = SearchResult_c
				.SearchResultInstances(Ooaofooa.getDefaultInstance(), null, false);
		for (int i = 0; i < searchResults.length; i++) {
			NonRootModelElement element = (NonRootModelElement) ModelSearchResult
					.getElementForResult(searchResults[i]);
			if (rt == ResultType.KEYLETTERS) {
				if ((element instanceof ModelClass_c) && ((ModelClass_c)element).getKey_lett().equals(itemName)) {
					return element;
				}
			} else {
				if (element.getName().equals(itemName)) {
					return element;
				}
			}
		}
		return null;
	}

	// Opens the first match in the results and returns the title of the opened editor
	public static WorkbenchPart openFirstMatch() throws PartInitException {
		NewSearchUI.activateSearchResultView();
	    ModelSearchResultPage msrp = (ModelSearchResultPage) NewSearchUI.getSearchResultView().getActivePage();	
	    msrp.setFocus();
		BaseTest.dispatchEvents(0);
	    msrp.gotoNextMatch();
		BaseTest.dispatchEvents(0);
	    msrp.displayMatch(msrp.getCurrentMatch());
		BaseTest.dispatchEvents(0);
		BaseTest.delay(2000);
        WorkbenchPart editorOpened = (WorkbenchPart) EditorUtil.getCurrentEditor();
        return editorOpened;
	}

	private static boolean dialogSettingsResult = false;
	
	public static boolean checkSearchDialogSettings(final String pattern, final boolean regEx,
			final boolean caseSensitive, final boolean oal, final boolean descriptions, final boolean names, final int scope,
			final String workingSet) {
		TestUtil.dismissShell(activeShell -> {
			dialogSettingsResult = false;
			Object data = activeShell.getData();
			if (data instanceof Dialog) {
				Control[] children = ((Dialog) data).getShell().getChildren();
				Combo combo = getPatternField((Composite) children[0]);
				Button[] buttons = getConfigurationButtons((Composite) children[0]);
				Button[] scopeButtons = getScopeButtons((Composite) children[0]);
				Button cancel = getButton((Composite) children[0], "Cancel");
				if (!combo.getText().equals(pattern)) {
					cancel.notifyListeners(SWT.Selection, new Event());
					return true;
				}
				for (int i = 0; i < buttons.length; i++) {
					if (buttons[i].getText().equals("Regular expression")) {
						if (buttons[i].getSelection() != regEx) {
							cancel.notifyListeners(SWT.Selection, new Event());
							return true;
						}
					} else if (buttons[i].getText().equals("Case sensitive")) {
						if (buttons[i].getSelection() != caseSensitive) {
							cancel.notifyListeners(SWT.Selection, new Event());
							return true;
						}
					} else if (buttons[i].getText().equals("Action Language")) {
						if (buttons[i].getSelection() != oal) {
							cancel.notifyListeners(SWT.Selection, new Event());
							return true;
						}
					} else if (buttons[i].getText().equals("Descriptions")) {
						if (buttons[i].getSelection() != descriptions) {
							cancel.notifyListeners(SWT.Selection, new Event());
							return true;
						}
					} else if (buttons[i].getText().equals("Element Names")) {
						if (buttons[i].getSelection() != names) {
							cancel.notifyListeners(SWT.Selection, new Event());
							return true;
						}
					}
				}
				for (int i = 0; i < scopeButtons.length; i++) {
					if (scopeButtons[i].getText().equals("&Workspace")
							&& scope == ISearchPageContainer.WORKSPACE_SCOPE) {
						if (scopeButtons[i].getSelection() != true) {
							cancel.notifyListeners(SWT.Selection, new Event());
							return true;
						}
					} else if (scopeButtons[i].getText().equals("&Workspace")
							&& scope != ISearchPageContainer.WORKSPACE_SCOPE) {
						// button should not be checked
						if (scopeButtons[i].getSelection() != false) {
							cancel.notifyListeners(SWT.Selection, new Event());
							return true;
						}
					}
					if (scopeButtons[i].getText().equals("Selecte&d resources")
							&& scope == ISearchPageContainer.SELECTION_SCOPE) {
						if (scopeButtons[i].getSelection() != true) {
							cancel.notifyListeners(SWT.Selection, new Event());
							return true;
						}
					} else if (scopeButtons[i].getText().equals("Selecte&d resources")
							&& scope != ISearchPageContainer.SELECTION_SCOPE) {
						if (scopeButtons[i].getSelection() != false) {
							cancel.notifyListeners(SWT.Selection, new Event());
							return true;
						}
					}
					if (scopeButtons[i].getText().equals("Enclosing pro&jects")
							&& scope == ISearchPageContainer.SELECTED_PROJECTS_SCOPE) {
						if (scopeButtons[i].getSelection() != true) {
							cancel.notifyListeners(SWT.Selection, new Event());
							return true;
						}
					} else if (scopeButtons[i].getText().equals("Enclosing pro&jects")
							&& scope != ISearchPageContainer.SELECTED_PROJECTS_SCOPE) {
						if (scopeButtons[i].getSelection() != false) {
							cancel.notifyListeners(SWT.Selection, new Event());
							return true;
						}
					}
					if (scopeButtons[i].getText().equals("Wor&king set:")
							&& scope == ISearchPageContainer.WORKING_SET_SCOPE) {
						if (scopeButtons[i].getSelection() != true) {
							cancel.notifyListeners(SWT.Selection, new Event());
							return true;
						}
						IWorkingSet[] selectedWorkingSets = ((SearchDialog) data).getSelectedWorkingSets();
						boolean found = false;
						for (int j = 0; j < selectedWorkingSets.length; j++) {
							IWorkingSet set = selectedWorkingSets[j];
							if (set.getName().equals(workingSet)) {
								found = true;
								break;
							}
						}
						if (!found) {
							cancel.notifyListeners(SWT.Selection, new Event());
							return true;
						}
					} else if (scopeButtons[i].getText().equals("Wor&king set:")
							&& scope != ISearchPageContainer.WORKING_SET_SCOPE) {
						if (scopeButtons[i].getSelection() != false) {
							cancel.notifyListeners(SWT.Selection, new Event());
							return true;
						}
					}
				}
				dialogSettingsResult = true;
				cancel.notifyListeners(SWT.Selection, new Event());
			}
			return true;
		});
		NewSearchUI.openSearchDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow(),
				"org.xtuml.bp.ui.search.xtumlSearchPage");
		return dialogSettingsResult;
	}

}
