//============================================================================
//
//	Copyright (c) 1999-2015 . All Rights Reserved.
//
//----------------------------------------------------------------------------
//
//	Fichero: ClusterGroupListener.java  1.0 21/1/00
//
//
//	Descripci�n: Interfaz ClusterGroupListener
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
//----------------------------------------------------------------------------

package org.clusternet;


/**
 * Esta interfaz es utilizada por la clase CGLThread para notificar
 * eventos acerca de la incorporaci�n/eliminaci�n de IDGLs en
 * la jerarqu�a de control.
 */

public interface ClusterGroupListener
{
  /**
   * Notifica que ClusterGroupID ha sido a�adido
   * @param clusterGroupID Nuevo ClusterGroupID que se puede alcanzar
   */
  public void IDGLA�adido(ClusterGroupID clusterGroupID);

  /**
   * Notifica que ClusterGroupID ha sido eliminado
   * @param clusterGroupID ClusterGroupID que ha quedado fuera de alcanze
   */
  public void IDGLEliminado(ClusterGroupID clusterGroupID);

}
