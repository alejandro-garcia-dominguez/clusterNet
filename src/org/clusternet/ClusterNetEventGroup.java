//============================================================
//
//	Copyright (c) 1999-2015 . All Rights Reserved.
//
//------------------------------------------------------------
//
//	File: ClusterNetEventGroup.java  1.0 14/03/2000
//
//
//	Description: Clase ClusterNetEventGroup. Evento ClusterNet ClusterGroupID
//
// 	Authors: 
//		 Alejandro Garc�a-Dom�nguez (alejandro.garcia.dominguez@gmail.com)
//		 Antonio Berrocal Piris (antonioberrocalpiris@gmail.com)
//
//  Historial: 
//  07.04.2015 Changed licence to Apache 2.0     
//
//  This file is part of ClusterNet 
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//------------------------------------------------------------

package org.clusternet;


import java.util.EventObject;
/**
 *  La clase ClusterNetEventGroup es utilizada por ClusterNet para notificar la incorporacion
 * o eliminacion de un ClusterGroupID
 */
public class ClusterNetEventGroup extends ClusterNetEvent
{

  /** ClusterGroupID */
  private ClusterGroupID clusterGroupID = null;

  /** Boolean bA�adido. especifica si el ClusterGroupID ha sido a�adido  o eliminado */
  private boolean bA�adido = false;

  /**
   * Constructor ClusterNetEvent
   * @param socket Un objeto SocketClusterNetImp
   * @param sInformativa cadena Informativa
   * @param evento El tipo de evento que se quiere crear
   */
  public ClusterNetEventGroup(SocketClusterNetImp socket,String sInformativa,ClusterGroupID clusterGroupID, boolean bA�adido)
  {
    super(socket,EVENTO_IDGL,sInformativa);
    this.clusterGroupID = clusterGroupID;
    this.bA�adido = bA�adido;
  }

  /**
   * Obtiene el ClusterGroupID
   * @return el objeto ClusterGroupID
   */
  public ClusterGroupID getIDGL(){return this.clusterGroupID;}

  /**
   * Boolean que indica si el ClusterGroupID ha sido a�adido o eliminado a la jerarqu�a
   * del Grupo Local
   */
  public boolean esA�adido() { return this.bA�adido;}

}

