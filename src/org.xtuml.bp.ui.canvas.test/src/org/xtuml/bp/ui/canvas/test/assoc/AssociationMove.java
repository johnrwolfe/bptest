//=====================================================================
//
// NOTE: This file was generated, but is maintained by hand.
// Generated by: UnitTestGenerator.pl
// Version:      1.15
// Matrix:       association_move.txt
//
//=====================================================================

package org.xtuml.bp.ui.canvas.test.assoc;

import java.util.UUID;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.xtuml.bp.core.*;
import org.xtuml.bp.core.common.BridgePointPreferencesStore;
import org.xtuml.bp.core.common.ClassQueryInterface_c;
import org.xtuml.bp.core.common.NonRootModelElement;
import org.xtuml.bp.core.common.PersistableModelComponent;
import org.xtuml.bp.core.util.WorkspaceUtil;
import org.xtuml.bp.test.common.*;
import org.xtuml.bp.ui.canvas.*;
import org.xtuml.bp.ui.graphics.editor.*;
import org.xtuml.bp.ui.graphics.parts.ConnectorEditPart;
import org.xtuml.bp.ui.canvas.test.*;

public class AssociationMove extends CanvasTest {
    public static boolean generateResults = false;
    public static boolean useDrawResults = false;

    String test_id = "";

    protected String getResultName() {
        return getClass().getSimpleName() + "_" + test_id;
    }

    protected GraphicalEditor fActiveEditor;
    private boolean diagramZoomed;
    private static boolean initialized;
    private ConnectorEditPart testPart;

    private String row_id = "";
    private String column_id = "";

    private Association_c testRel;
    private UUID testOirId;

    private int originalRelNum;
    private String originalPhrase;
    private int originalCond;
    private int originalMult;

    protected GraphicalEditor getActiveEditor() {
        return fActiveEditor;
    }

    public AssociationMove(String subTypeClassName, String subTypeArg0) {
        super(subTypeClassName, subTypeArg0);
    }

    protected String getTestId(String src, String dest, String count) {
        column_id = src;
        row_id = dest;
        return "test_" + count;
    }
    
    @Before
    public void setUp() throws Exception {
        super.setUp();
        CanvasTransactionListener.disableReconciler();
        ConnectorEditPart.setToleranceForTests(15);
        // load the test model
        if (!initialized) {
            // set the core plugin to debugging as
            // there are some benign consistency errors
            // causing failures
            // These errors will be resolved when full
            // generic package support is complete
            CorePlugin.getDefault().setDebugging(true);
            
            WorkspaceUtil.setAutobuilding(false);

            loadProject("AssociationMoveTests");

            CorePlugin.disableParseAllOnResourceChange();

            PersistableModelComponent sys_comp = m_sys
                    .getPersistableComponent();
            sys_comp.loadComponentAndChildren(new NullProgressMonitor());
            initialized = true;
        } else {
            // undo the last change
            if (m_sys.getTransactionManager().getUndoAction().isEnabled()) {
                m_sys.getTransactionManager().getUndoAction().run();
            }
        }
    }

    @After
    public void tearDown() throws Exception {
        super.tearDown();
        diagramZoomed = false;
        fActiveEditor = null;
        ConnectorEditPart.setToleranceForTests(-1);
        PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
                .closeAllEditors(false);
        CanvasTransactionListener.enableReconciler();
    }
    
    private Package_c getTestPackage() {
        Package_c result = Package_c.getOneEP_PKGOnR1401(m_sys, new ClassQueryInterface_c() {
            @Override
            public boolean evaluate(Object selected) {
                return ((Package_c)selected).getName().equals(column_id);
            }
        });
        return result;
    }
    
    private boolean targetIsReflexive( ClassInAssociation_c src_oir, final ModelClass_c target_obj ) {
        // if the oir and target object are the same, then return false
        if ( null == src_oir || null == target_obj || src_oir.getObj_id().equals(target_obj.getObj_id())) return false;

        // participant to participant
        ClassAsSimpleParticipant_c part = ClassAsSimpleParticipant_c.getOneR_PARTOnR207(SimpleAssociation_c.getOneR_SIMPOnR207(
                ClassAsSimpleParticipant_c.getOneR_PARTOnR204(ReferredToClassInAssoc_c.getOneR_RTOOnR203(src_oir))), new ClassQueryInterface_c() {
            @Override
            public boolean evaluate( Object candidate ) {
                return ((ClassAsSimpleParticipant_c)candidate).getObj_id().equals(target_obj.getObj_id());
            }
        });
        if ( part != null ) return true;
        
        // formalizer to participant
        part = ClassAsSimpleParticipant_c.getOneR_PARTOnR207(SimpleAssociation_c.getOneR_SIMPOnR208(ClassAsSimpleFormalizer_c.getOneR_FORMOnR205(
                ReferringClassInAssoc_c.getOneR_RGOOnR203(src_oir))));
        if ( part != null && part.getObj_id().equals(target_obj.getObj_id()) ) return true;
        
        // participant to formalizer
        ClassAsSimpleFormalizer_c form = ClassAsSimpleFormalizer_c.getOneR_FORMOnR208(SimpleAssociation_c.getOneR_SIMPOnR207(
                ClassAsSimpleParticipant_c.getOneR_PARTOnR204(ReferredToClassInAssoc_c.getOneR_RTOOnR203(src_oir))));
        if ( form != null && form.getObj_id().equals(target_obj.getObj_id()) ) return true;
        
        // one to other
       ClassAsAssociatedOtherSide_c other = ClassAsAssociatedOtherSide_c.getOneR_AOTHOnR210(LinkedAssociation_c.getOneR_ASSOCOnR209(
               ClassAsAssociatedOneSide_c.getOneR_AONEOnR204(ReferredToClassInAssoc_c.getOneR_RTOOnR203(src_oir))));
        if ( other != null && other.getObj_id().equals(target_obj.getObj_id()) ) return true;

        // other to one
       ClassAsAssociatedOneSide_c one = ClassAsAssociatedOneSide_c.getOneR_AONEOnR209(LinkedAssociation_c.getOneR_ASSOCOnR210(
               ClassAsAssociatedOtherSide_c.getOneR_AOTHOnR204(ReferredToClassInAssoc_c.getOneR_RTOOnR203(src_oir))));
        if ( one != null && one.getObj_id().equals(target_obj.getObj_id()) ) return true;

        return false;
    }
    
    private Graphconnector_c getAnchorFromR_OIR( ClassInAssociation_c oir ) {
        NonRootModelElement representsCon = Association_c.getOneR_RELOnR201(oir);
        ClassAsLink_c assr = ClassAsLink_c.getOneR_ASSROnR205(ReferringClassInAssoc_c.getOneR_RGOOnR203(oir));
        if ( null != assr ) representsCon = assr;
        else {
            ClassAsSubtype_c sub = ClassAsSubtype_c.getOneR_SUBOnR205(ReferringClassInAssoc_c.getOneR_RGOOnR203(oir));
            if ( null != sub ) representsCon = sub;
        }
        NonRootModelElement representsShape = ImportedClass_c.getOneO_IOBJOnR202(oir);
        if ( null == representsShape ) representsShape = ModelClass_c.getOneO_OBJOnR201(oir);

        // get all graphical elements on the diagram
        GraphicalElement_c[] elements = GraphicalElement_c.getManyGD_GEsOnR1(fActiveEditor.getModel());
        
        // get the connector for the relationship
        Graphedge_c edge = null;
        for(int i = 0; i < elements.length; i++) {
            if(elements[i].getRepresents() == representsCon) {
                edge = Graphedge_c.getOneDIM_EDOnR20(Connector_c.getOneGD_CONOnR2(elements[i]));
            }
        }

        // get the shape for the relationship
        Graphnode_c node = null;
        for(int i = 0; i < elements.length; i++) {
            if(elements[i].getRepresents() == representsShape) {
                node = Graphnode_c.getOneDIM_NDOnR19(Shape_c.getOneGD_SHPOnR2(elements[i]));
            }
        }
        assertNotNull("Unable to find graphical elements for testing.", edge);
        assertNotNull("Unable to find graphical elements for testing.", node);
        
        // get the anchor between them
        Graphconnector_c anchor = Graphconnector_c.getOneDIM_CONOnR320(edge);
        if ( null == anchor || !anchor.getElementid().equals(node.getElementid()) ) {
            anchor = Graphconnector_c.getOneDIM_CONOnR321(edge);
            if ( null == anchor || !anchor.getElementid().equals(node.getElementid()) ) anchor = null;
        }

        assertNotNull("Unable to find graphical elements for testing.", anchor);

        return anchor;
    }
    
    private Graphnode_c getShapeFromPE_PE( PackageableElement_c pe_pe ) {
        NonRootModelElement representsShape = ImportedClass_c.getOneO_IOBJOnR8001(pe_pe);
        if ( null == representsShape ) representsShape = ModelClass_c.getOneO_OBJOnR8001(pe_pe);

        // get all graphical elements on the diagram
        GraphicalElement_c[] elements = GraphicalElement_c.getManyGD_GEsOnR1(fActiveEditor.getModel());

        // get the shape for the relationship
        Graphnode_c node = null;
        for(int i = 0; i < elements.length; i++) {
            if(elements[i].getRepresents() == representsShape) {
                node = Graphnode_c.getOneDIM_NDOnR19(Shape_c.getOneGD_SHPOnR2(elements[i]));
            }
        }

        assertNotNull("Unable to find graphical elements for testing.", node);

        return node;
    }

    private ConnectorEditPart getTestPart(NonRootModelElement instance) {
        if ( instance instanceof ClassInAssociation_c ) {
            ClassAsLink_c assr = ClassAsLink_c.getOneR_ASSROnR205(ReferringClassInAssoc_c.getOneR_RGOOnR203((ClassInAssociation_c)instance));
            if ( null != assr ) instance = assr;
            else {
                ClassAsSubtype_c sub = ClassAsSubtype_c.getOneR_SUBOnR205(ReferringClassInAssoc_c.getOneR_RGOOnR203((ClassInAssociation_c)instance));
                if ( null != sub ) instance = sub;
                else {
                    instance = Association_c.getOneR_RELOnR201((ClassInAssociation_c)instance);
                }
            }
        }
        if (testPart == null) {
            // open the editor for the test
            fActiveEditor = UITestingUtilities.getGraphicalEditorFor( instance, false, true );
            adjustZoom();
            testPart = (ConnectorEditPart) UITestingUtilities.getEditorPartFor( getConnectorInstance(instance) );
        }
        return testPart;
    }

    private Connector_c getConnectorInstance(NonRootModelElement testElement) {
        GraphicalElement_c[] elements = GraphicalElement_c.getManyGD_GEsOnR1(fActiveEditor.getModel());
        GraphicalElement_c element = null;
        for(int i = 0; i < elements.length; i++) {
            if(elements[i].getRepresents() == testElement) {
                element = elements[i];
            }
        }
        assertNotNull("Unable to find graphical element for testing.", element);
        return Connector_c.getOneGD_CONOnR2(element);
    }

    private void adjustZoom() {
        // we want to zoom all, then set zoom to 100% for
        // easier positional calculations
        // fill the available space with the editor
        // as nothing else is interesting to this
        // test
        if(!PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().isPageZoomed()) {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().toggleZoom(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePartReference());
        }
        if(!diagramZoomed) { 
            BaseTest.dispatchEvents(0);
            // disable grid snapping to allow exact
            // positions
            CorePlugin.getDefault().getPreferenceStore().setValue(BridgePointPreferencesStore.SNAP_TO_GRID, false);
            getActiveEditor().configureGridOptions();
            getActiveEditor().zoomAll();
            while (PlatformUI.getWorkbench().getDisplay().readAndDispatch());
            diagramZoomed = true;
        }
    }
    
    private void moveAssociation( ConnectorEditPart editPart, Graphconnector_c anchor, Graphnode_c node ) {
        UITestingUtilities.clearGraphicalSelection();
        UITestingUtilities.addElementToGraphicalSelection(editPart);

        // get source and destination point
        Point srcPoint = new Point( (int)anchor.getPositionx(), (int)anchor.getPositiony() );
        editPart.getFigure().translateToAbsolute(srcPoint);
        Graphelement_c element = Graphelement_c.getOneDIM_GEOnR301(node);
        Point dstPoint = new Point( (int)(element.getPositionx()), (int)(element.getPositiony()) );
        editPart.getFigure().translateToAbsolute(dstPoint);
        dstPoint.translate( (int)(node.getWidth() / 2), (int)(node.getHeight() / 2) );
        
        // move the mouse
        UITestingUtilities.doMouseMove( srcPoint.x, srcPoint.y );
        UITestingUtilities.doMousePress( srcPoint.x, srcPoint.y );
        UITestingUtilities.doMouseMove( dstPoint.x, dstPoint.y );
        UITestingUtilities.doMouseRelease( dstPoint.x, dstPoint.y );
    }
    
    private void cacheRelInfo( ClassInAssociation_c oir ) {
        Association_c rel = Association_c.getOneR_RELOnR201(oir);
        originalRelNum = rel.getNumb();
        ClassAsSimpleParticipant_c part = ClassAsSimpleParticipant_c.getOneR_PARTOnR204(ReferredToClassInAssoc_c.getOneR_RTOOnR203(oir));
        ClassAsSimpleFormalizer_c form = ClassAsSimpleFormalizer_c.getOneR_FORMOnR205(ReferringClassInAssoc_c.getOneR_RGOOnR203(oir));
        ClassAsAssociatedOneSide_c one = ClassAsAssociatedOneSide_c.getOneR_AONEOnR204(ReferredToClassInAssoc_c.getOneR_RTOOnR203(oir));
        ClassAsAssociatedOtherSide_c other = ClassAsAssociatedOtherSide_c.getOneR_AOTHOnR204(ReferredToClassInAssoc_c.getOneR_RTOOnR203(oir));
        ClassAsLink_c assr = ClassAsLink_c.getOneR_ASSROnR205(ReferringClassInAssoc_c.getOneR_RGOOnR203(oir));
        if ( null != part ) {
            originalPhrase = part.getTxt_phrs();
            originalCond = part.getCond();
            originalMult = part.getMult();
        }
        else if ( null != form ){
            originalPhrase = form.getTxt_phrs();
            originalCond = form.getCond();
            originalMult = form.getMult();
        }
        else if ( null != one ){
            originalPhrase = one.getTxt_phrs();
            originalCond = one.getCond();
            originalMult = one.getMult();
        }
        else if ( null != other ){
            originalPhrase = other.getTxt_phrs();
            originalCond = other.getCond();
            originalMult = other.getMult();
        }
        else if ( null != assr ){
            originalMult = assr.getMult();
        }
    }

    /**
     * "ABC" is one of the degrees of freedom as specified in this issues
     * test matrix.
     * This routine gets the "ABC" instance from the given name.
     * 
     * @param element The degree of freedom instance to retrieve
     * @return A model element used in the test as specified by the test matrix
     */
    NonRootModelElement selectABC(String element, Object extraData) {
        NonRootModelElement nrme = null;
        Package_c testPackage = getTestPackage();

        // select an OIR with the combination of all three tests
        nrme = ClassInAssociation_c.getOneR_OIROnR201(Association_c.getManyR_RELsOnR8001(PackageableElement_c.getManyPE_PEsOnR8000(testPackage)),
                new ClassQueryInterface_c() {
                    @Override
                    public boolean evaluate(Object candidate) {
                        final ClassInAssociation_c selected = (ClassInAssociation_c)candidate;
                        boolean oir_type_test;
                        boolean rel_formalized_test;
                        boolean oir_imported_test;
                        if (element.contains("A1")) {
                            // select all simple participants
                            ClassAsSimpleParticipant_c part = ClassAsSimpleParticipant_c.getOneR_PARTOnR204(ReferredToClassInAssoc_c.getOneR_RTOOnR203(selected));
                            oir_type_test = part != null;
                        }
                        else if (element.contains("A2")) {
                            // select all simple formalizers
                            ClassAsSimpleFormalizer_c form = ClassAsSimpleFormalizer_c.getOneR_FORMOnR205(ReferringClassInAssoc_c.getOneR_RGOOnR203(selected));
                            oir_type_test = form != null;
                        }
                        else if (element.contains("A3")) {
                            // select all associative ones
                            ClassAsAssociatedOneSide_c one = ClassAsAssociatedOneSide_c.getOneR_AONEOnR204(ReferredToClassInAssoc_c.getOneR_RTOOnR203(selected));
                            oir_type_test = one != null;
                        }
                        else if (element.contains("A4")) {
                            // select all associative others
                            ClassAsAssociatedOtherSide_c other = ClassAsAssociatedOtherSide_c.getOneR_AOTHOnR204(ReferredToClassInAssoc_c.getOneR_RTOOnR203(selected));
                            oir_type_test = other != null;
                        }
                        else if (element.contains("A5")) {
                            // select all associative links
                            ClassAsLink_c assr = ClassAsLink_c.getOneR_ASSROnR205(ReferringClassInAssoc_c.getOneR_RGOOnR203(selected));
                            oir_type_test = assr != null;
                        }
                        else if (element.contains("A6")) {
                            // select all supertypes
                            ClassAsSupertype_c sup = ClassAsSupertype_c.getOneR_SUPEROnR204(ReferredToClassInAssoc_c.getOneR_RTOOnR203(selected));
                            oir_type_test = sup != null;
                        }
                        else if (element.contains("A7")) {
                            // select all subtypes
                            ClassAsSubtype_c sub = ClassAsSubtype_c.getOneR_SUBOnR205(ReferringClassInAssoc_c.getOneR_RGOOnR203(selected));
                            oir_type_test = sub != null;
                        }
                        else {
                            oir_type_test = false;
                        }
                        if (element.contains("B1")) {
                            // select all formalized relationships
                            Association_c rel = Association_c.getOneR_RELOnR201(selected);
                            rel_formalized_test = rel.Isformalized();
                        }
                        else if (element.contains("B2")) {
                            // select all formalized relationships
                            Association_c rel = Association_c.getOneR_RELOnR201(selected);
                            rel_formalized_test = !rel.Isformalized();
                        }
                        else {
                            rel_formalized_test = false;
                        }
                        if (element.contains("C1")) {
                            // select all classes that are imported
                            ImportedClass_c iobj = ImportedClass_c.getOneO_IOBJOnR202(selected);
                            oir_imported_test = iobj != null;
                        }
                        else if (element.contains("C2")) {
                            // select all classes that are imported
                            ImportedClass_c iobj = ImportedClass_c.getOneO_IOBJOnR202(selected);
                            oir_imported_test = iobj == null;
                        }
                        else if (element.contains("C3")) {
                            // don't specify imported or not
                            oir_imported_test = true;
                        }
                        else {
                            oir_imported_test = false;
                        }
                        return oir_type_test && rel_formalized_test && oir_imported_test;
                    }
                });

        assertTrue("An instance with degree of freedom type \"ABC\" was not found.  Instance Name: " + element + ".", nrme!=null);
        testRel = Association_c.getOneR_RELOnR201((ClassInAssociation_c)nrme);
        testOirId = ((ClassInAssociation_c)nrme).getOir_id();
        return nrme;
    }

    /**
     * "DEFC" is one of the degrees of freedom as specified in this issues
     * test matrix.
     * This routine gets the "DEFC" instance from the given name.
     * 
     * @param element The degree of freedom instance to retrieve
     * @return A model element used in the test as specified by the test matrix
     */
    NonRootModelElement selectDEFC(String element, Object extraData) {
        NonRootModelElement nrme = null;
        ClassInAssociation_c src_oir = (ClassInAssociation_c)extraData;
        Package_c testPackage = getTestPackage();

        // select a packageable element
        nrme = PackageableElement_c.getOnePE_PEOnR8000( testPackage, new ClassQueryInterface_c() {
            @Override
            public boolean evaluate(Object candidate) {
                boolean imported = false;
                ModelClass_c selected = ModelClass_c.getOneO_OBJOnR8001((PackageableElement_c)candidate);
                if ( null == selected ) {
                    imported = true;
                    selected = ModelClass_c.getOneO_OBJOnR101(ImportedClass_c.getOneO_IOBJOnR8001((PackageableElement_c)candidate));
                    if ( null == selected) return false; // not an IOBJ or OBJ
                }
                if ( selected.getObj_id().equals(src_oir.getObj_id()) ) return false; // disallow selecting the same object as the source

                boolean obj_target_test;
                boolean reflexive_test;
                boolean obj_imported_test;
                if (element.contains("D1")) {
                    // select any obj
                    obj_target_test = true;
                }
                else if (element.contains("D2")) {
                    // select an associative link in the same relationship
                    ClassAsLink_c assr = ClassAsLink_c.getOneR_ASSROnR211(LinkedAssociation_c.getOneR_ASSOCOnR206(Association_c.getOneR_RELOnR201(src_oir)));
                    obj_target_test = assr != null && selected.getObj_id().equals(assr.getObj_id());
                }
                else if (element.contains("D3")) {
                    // select an associative one in the same relationship
                    ClassAsAssociatedOneSide_c one = ClassAsAssociatedOneSide_c.getOneR_AONEOnR209(LinkedAssociation_c.getOneR_ASSOCOnR206(Association_c.getOneR_RELOnR201(src_oir)));
                    obj_target_test = one != null && selected.getObj_id().equals(one.getObj_id());
                }
                else if (element.contains("D4")) {
                    // select an associative other in the same relationship
                    ClassAsAssociatedOtherSide_c other = ClassAsAssociatedOtherSide_c.getOneR_AOTHOnR210(LinkedAssociation_c.getOneR_ASSOCOnR206(Association_c.getOneR_RELOnR201(src_oir)));
                    obj_target_test = other != null && selected.getObj_id().equals(other.getObj_id());
                }
                else if (element.contains("D5")) {
                    // select a supertype in the same relationship
                    ClassAsSupertype_c sup = ClassAsSupertype_c.getOneR_SUPEROnR212(SubtypeSupertypeAssociation_c.getOneR_SUBSUPOnR206(Association_c.getOneR_RELOnR201(src_oir)));
                    obj_target_test = sup != null && selected.getObj_id().equals(sup.getObj_id());
                }
                else if (element.contains("D6")) {
                    // select a subtype in the same relationship
                    final UUID selected_id = selected.getObj_id();
                    ClassAsSubtype_c sub = ClassAsSubtype_c.getOneR_SUBOnR213(SubtypeSupertypeAssociation_c.getOneR_SUBSUPOnR206(
                            Association_c.getOneR_RELOnR201(src_oir)), new ClassQueryInterface_c() {
                        @Override
                        public boolean evaluate( Object candidate ) {
                            return ((ClassAsSubtype_c)candidate).getObj_id().equals(selected_id);
                        }
                    });
                    obj_target_test = sub != null;
                }
                else {
                    obj_target_test = false;
                }
                if (element.contains("E1")) {
                    // select reflexive targets
                    reflexive_test = targetIsReflexive( src_oir, selected );
                }
                else if (element.contains("E2")) {
                    // select non-reflexive targets
                    reflexive_test = !targetIsReflexive( src_oir, selected );
                }
                else {
                    reflexive_test = false;
                }
                if (element.contains("C1")) {
                    // select all classes that are imported
                    obj_imported_test = imported;
                }
                else if (element.contains("C2")) {
                    // select all classes that are imported
                    obj_imported_test = !imported;
                }
                else if (element.contains("C3")) {
                    // don't specify imported or not
                    obj_imported_test = true;
                }
                else {
                    obj_imported_test = false;
                }
                return obj_target_test && reflexive_test && obj_imported_test;
            }
        });

        assertTrue("An instance with degree of freedom type \"DEFC\" was not found.  Instance Name: " + element + ".", nrme!=null);
        return nrme;
    }

    /**
     * This routine performs the action associated with a matrix cell.
     * The parameters represent model instances aquired based on the specifed
     * column instance and row instance.
     * 
     * @param columnInstance Model instance from the column
     * @param rowInstance Model instance from the row
     */
    void ABC_DEFC_Action(NonRootModelElement columnInstance, NonRootModelElement rowInstance) {
        // cache the relationship info
        cacheRelInfo( (ClassInAssociation_c)columnInstance );

        // set the routing style
        if (row_id.contains("F1")) {
            CorePlugin.getDefault().getPreferenceStore().setValue( BridgePointPreferencesStore.DEFAULT_ROUTING_STYLE,
                                                                   BridgePointPreferencesStore.RECTILINEAR_ROUTING );
        }
        else if (row_id.contains("F2")) {
            CorePlugin.getDefault().getPreferenceStore().setValue( BridgePointPreferencesStore.DEFAULT_ROUTING_STYLE,
                                                                   BridgePointPreferencesStore.OBLIQUE_ROUTING );
        }
        
        // get the edit part for the connector, the anchor and node
        ConnectorEditPart testPart = getTestPart( columnInstance );
        Graphconnector_c src_anchor = getAnchorFromR_OIR((ClassInAssociation_c)columnInstance);
        Graphnode_c dest_node = getShapeFromPE_PE((PackageableElement_c)rowInstance);
        
        // perform move
        moveAssociation( testPart, src_anchor, dest_node );
        BaseTest.dispatchEvents(50);
    }

    /**
    * This function verifies an expected result.
    *
    * @param source A model element instance aquired through a action taken
    *               on a column of the matrix.
    * @param destination A model element instance aquired through a action taken
    *                    taken on a row of the matrix.
    * @return true if the test succeeds, false if it fails
    */
    boolean checkResult_assocInfoSame(NonRootModelElement source, NonRootModelElement destination) {
        boolean assocInfoSame = true; // start as true. "and" with each following result. if any result is false, the final result will be false
        
        if ( null != testRel ) {
            // check rel num
            assocInfoSame &= originalRelNum == testRel.getNumb();

            // get oir
            ClassInAssociation_c oir = ClassInAssociation_c.ClassInAssociationInstance(testRel.getModelRoot(), new ClassQueryInterface_c() {
                @Override
                public boolean evaluate( Object candidate ) {
                    return ((ClassInAssociation_c)candidate).getOir_id().equals(testOirId);
                }
            });

            // check other rel info
            ClassAsSimpleParticipant_c part = ClassAsSimpleParticipant_c.getOneR_PARTOnR204(ReferredToClassInAssoc_c.getOneR_RTOOnR203(oir));
            ClassAsSimpleFormalizer_c form = ClassAsSimpleFormalizer_c.getOneR_FORMOnR205(ReferringClassInAssoc_c.getOneR_RGOOnR203(oir));
            ClassAsAssociatedOneSide_c one = ClassAsAssociatedOneSide_c.getOneR_AONEOnR204(ReferredToClassInAssoc_c.getOneR_RTOOnR203(oir));
            ClassAsAssociatedOtherSide_c other = ClassAsAssociatedOtherSide_c.getOneR_AOTHOnR204(ReferredToClassInAssoc_c.getOneR_RTOOnR203(oir));
            ClassAsLink_c assr = ClassAsLink_c.getOneR_ASSROnR205(ReferringClassInAssoc_c.getOneR_RGOOnR203(oir));
            if ( null != part ) {
                assocInfoSame &= originalPhrase.equals(part.getTxt_phrs());
                assocInfoSame &= originalCond == part.getCond();
                assocInfoSame &= originalMult == part.getMult();
            }
            else if ( null != form ){
                assocInfoSame &= originalPhrase.equals(form.getTxt_phrs());
                assocInfoSame &= originalCond == form.getCond();
                assocInfoSame &= originalMult == form.getMult();
            }
            else if ( null != one ){
                assocInfoSame &= originalPhrase.equals(one.getTxt_phrs());
                assocInfoSame &= originalCond == one.getCond();
                assocInfoSame &= originalMult == one.getMult();
            }
            else if ( null != other ){
                assocInfoSame &= originalPhrase.equals(other.getTxt_phrs());
                assocInfoSame &= originalCond == other.getCond();
                assocInfoSame &= originalMult == other.getMult();
            }
            else if ( null != assr ){
                assocInfoSame &= originalMult == assr.getMult();
            }
        }
        else assocInfoSame &= false;

        // return check
        return assocInfoSame;
    }


    /**
    * This function verifies an expected result.
    *
    * @param source A model element instance aquired through a action taken
    *               on a column of the matrix.
    * @param destination A model element instance aquired through a action taken
    *                    taken on a row of the matrix.
    * @return true if the test succeeds, false if it fails
    */
    boolean checkResult_moveDisallowed(NonRootModelElement source, NonRootModelElement destination) {
        boolean moveDisallowed = false;
        //TODO: Implement
        moveDisallowed = true;
        return moveDisallowed;
    }


    /**
    * This function verifies an expected result.
    *
    * @param source A model element instance aquired through a action taken
    *               on a column of the matrix.
    * @param destination A model element instance aquired through a action taken
    *                    taken on a row of the matrix.
    * @return true if the test succeeds, false if it fails
    */
    boolean checkResult_assocUnformal(NonRootModelElement source, NonRootModelElement destination) {
        boolean assocUnformal = false;
        assocUnformal = ( null != testRel && !testRel.Isformalized() );
        return assocUnformal;
    }


    /**
    * This function verifies an expected result.
    *
    * @param source A model element instance aquired through a action taken
    *               on a column of the matrix.
    * @param destination A model element instance aquired through a action taken
    *                    taken on a row of the matrix.
    * @return true if the test succeeds, false if it fails
    */
    boolean checkResult_rectilinearCheck(NonRootModelElement source, NonRootModelElement destination) {
        boolean rectilinearCheck = false;
        //TODO: Implement
        rectilinearCheck = true;
        return rectilinearCheck;
    }


    /**
    * This function verifies an expected result.
    *
    * @param source A model element instance aquired through a action taken
    *               on a column of the matrix.
    * @param destination A model element instance aquired through a action taken
    *                    taken on a row of the matrix.
    * @return true if the test succeeds, false if it fails
    */
    boolean checkResult_moveComplete(NonRootModelElement source, NonRootModelElement destination) {
        boolean moveComplete = false;

        // get oir in this rel that is the same as the dst object
        final ModelClass_c dstObj;
        if ( null == ImportedClass_c.getOneO_IOBJOnR8001((PackageableElement_c)destination) ) {
            dstObj = ModelClass_c.getOneO_OBJOnR8001((PackageableElement_c)destination);
        }
        else dstObj = ModelClass_c.getOneO_OBJOnR101(ImportedClass_c.getOneO_IOBJOnR8001((PackageableElement_c)destination));
        if ( null != dstObj ) {
            ClassInAssociation_c[] oirs = ClassInAssociation_c.getManyR_OIRsOnR201(testRel, new ClassQueryInterface_c() {
                @Override
                public boolean evaluate( Object candidate ) {
                    return ((ClassInAssociation_c)candidate).getObj_id().equals(dstObj.getObj_id());
                }
            });
            if ( ( oirs.length == 1 && row_id.contains("E2") ) || ( oirs.length == 2 && row_id.contains("E1") ) ) {
                moveComplete = true;
            }
        }

        return moveComplete;
    }
}
