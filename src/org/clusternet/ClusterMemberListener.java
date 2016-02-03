//============================================================================
//
//	Copyright (c) 1999-2015 . All Rights Reserved.
//
//----------------------------------------------------------------------------
//
//	File: ClusterMemberListener.java  1.0 21/10/99
//
//
//	Descripci�n: Interfaz ClusterMemberListener
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
 * eventos acerca de la incorporaci�n/eliminaci�n de sockets en el grupo local.-
 */

public interface ClusterMemberListener
{
  /**
   * Notifica que Id_Socket ha sido a�adido al grupo local
   * @param id_socket ClusterMemberID a�adido al grupo local
   */
  public void ID_SocketA�adido(ClusterMemberID id_socket);

  /**
   * Notifica que Id_Socket ha sido eliminado
   * @param id_socket ClusterMemberID eliminado del Grupo Local
   */
  public void ID_SocketEliminado(ClusterMemberID id_socket);
}
