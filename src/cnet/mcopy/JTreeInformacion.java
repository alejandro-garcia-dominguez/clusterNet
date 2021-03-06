package cnet.mcopy;

/**
Copyright (c) 2000-2014 . All Rights Reserved.
@Autor: Alejandro Garc�a Dom�nguez alejandro.garcia.dominguez@gmail.com   alejandro@iacobus.com
       Antonio Berrocal Piris antonioberrocalpiris@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/



import java.awt.*;

import javax.swing.*;
import javax.swing.tree.*;

import cnet.*;

import javax.swing.event.*;

import java.util.Enumeration;

/**
 * Esta clase implementa el �rbol de informaci�n de ClusterNet.
 * <p>Title: runGUI</p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2003</p>
 * <p>Company: </p>
 * @author unascribed
 * @version 1.1
 */
public class JTreeInformacion extends JPanel //implements TreeModel
{
    protected DefaultMutableTreeNode rootNode;
    protected DefaultMutableTreeNode nodoIDGL = null;
    protected DefaultMutableTreeNode nodoIDSocket = null;

    protected DefaultTreeModel treeModel;
    protected JTree tree;
    private Toolkit toolkit = Toolkit.getDefaultToolkit();

    public JTreeInformacion() {
        rootNode = new DefaultMutableTreeNode("ClusterNet");
        treeModel = new DefaultTreeModel(rootNode);
        //treeModel.addTreeModelListener(new MyTreeModelListener());

        tree = new JTree(treeModel);
        tree.setEditable(true);
        tree.getSelectionModel().setSelectionMode
                (TreeSelectionModel.SINGLE_TREE_SELECTION);
        tree.setShowsRootHandles(true);

        //Nodo IDGLs
        nodoIDGL = new DefaultMutableTreeNode("Groups");
        rootNode.add(nodoIDGL);

        //Nodo IDSockets
        nodoIDSocket = new DefaultMutableTreeNode("Members");
        rootNode.add(nodoIDSocket);


        JScrollPane scrollPane = new JScrollPane(tree);
        setLayout(new GridLayout(1,0));
        add(scrollPane);
    }

    /** Remove all nodes except the root node. */
    public void clear() {
        this.nodoIDGL.removeAllChildren();
        this.nodoIDSocket.removeAllChildren();
        treeModel.reload();
    }

    /** Remove the currently selected node. */
    public void removeCurrentNode() {
        TreePath currentSelection = tree.getSelectionPath();
        if (currentSelection != null) {
            DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode)
                         (currentSelection.getLastPathComponent());
            MutableTreeNode parent = (MutableTreeNode)(currentNode.getParent());
            if (parent != null) {
                treeModel.removeNodeFromParent(currentNode);
                return;
            }
        }

        // Either there was no selection, or the root was selected.
        toolkit.beep();
    }


    /**
     * A�ade un ClusterGroupID al �rbol
     */
    public DefaultMutableTreeNode addIDGL(ClusterGroupID clusterGroupID)
    {
       return addObject(nodoIDGL, clusterGroupID, true);
    }

    /**
     * A�ade un ClusterMemberID al �rbol
     */
    public DefaultMutableTreeNode addID_Socket(ClusterMemberID idSocket)
    {
       return addObject(nodoIDSocket, idSocket, true);
    }

   /**
     * Eliminar un ClusterGroupID al �rbol
     */
    public void removeIDGL(ClusterGroupID clusterGroupID)
    {

      Enumeration nodosIDGLS =  nodoIDGL.children();

      while(nodosIDGLS.hasMoreElements())
      {
        DefaultMutableTreeNode nodo =  (DefaultMutableTreeNode) nodosIDGLS.nextElement();

        ClusterGroupID nodoIDGL =  (ClusterGroupID) nodo.getUserObject();

        if (nodoIDGL.equals(clusterGroupID))
        {
          treeModel.removeNodeFromParent(nodo);
        }
      }
    }


    /**
     * Eliminar un IDSocket al �rbol
     */
    public void removeIDSocket(ClusterMemberID idSocket)
    {
      Enumeration nodosIDSockets =  this.nodoIDSocket.children();

      while(nodosIDSockets.hasMoreElements())
      {
        DefaultMutableTreeNode nodo =  (DefaultMutableTreeNode) nodosIDSockets.nextElement();

        ClusterMemberID nodoIDSocket=  (ClusterMemberID) nodo.getUserObject();

        if (nodoIDSocket.equals(idSocket))
        {
          treeModel.removeNodeFromParent(nodo);
        }
      }
    }


    /** Add child to the currently selected node. */
    public DefaultMutableTreeNode addObject(Object child) {
        DefaultMutableTreeNode parentNode = null;
        TreePath parentPath = tree.getSelectionPath();

        if (parentPath == null) {
            parentNode = rootNode;
        } else {
            parentNode = (DefaultMutableTreeNode)
                         (parentPath.getLastPathComponent());
        }

        return addObject(parentNode, child, true);
    }

    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child) {
        return addObject(parent, child, false);
    }


    /**
     * A�ade un nodo al �rbol dado un nodo padre
     * @param parent
     * @param child
     * @param shouldBeVisible
     * @return
     */
    public DefaultMutableTreeNode addObject(DefaultMutableTreeNode parent,
                                            Object child,
                                            boolean shouldBeVisible) {
        DefaultMutableTreeNode childNode =
                new DefaultMutableTreeNode(child);

        if (parent == null) {
            parent = rootNode;
        }

        treeModel.insertNodeInto(childNode, parent,
                                 parent.getChildCount());

        // Make sure the user can see the lovely new node.
        if (shouldBeVisible) {
            tree.scrollPathToVisible(new TreePath(childNode.getPath()));
        }
        return childNode;
    }



    // ============  Interfaz  TreeModel ==============
    public void addTreeModelListener(TreeModelListener l)
    {
      ;
    }

    public void removeTreeModelListener(TreeModelListener l)
    {
      ;
    }

  public int getIndexOfChild(Object parent,
                           Object child)
  {

   return 0;
  }

  public void valueForPathChanged(TreePath path,
                                Object newValue)
  {
    ;
  }


   public boolean isLeaf(Object node)
   {
     return true;
   }

   public int getChildCount(Object parent)
   {
      return 0;
   }

   public void getChild(Object parent,
                       int index)
   {
    ;
   }

   public void getRoot()
   {
      ;
   }
}

