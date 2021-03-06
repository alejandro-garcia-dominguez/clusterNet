//============================================================
//
//	Copyright (c) 1999-2015 . All Rights Reserved.
//
//------------------------------------------------------------
//
//	File: ClusterNetEventError.java  1.0 14/03/2000
//
//
//	Description: Clase ClusterNetEventError. Evento ClusterNet Error
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

package cnet;


import java.util.EventObject;

/**
 * La clase ClusterNetEventError es utilizada por ClusterNet para notificar de un error
 * en el protocolo.
 */
public class ClusterNetEventError extends ClusterNetEvent
{
  /** Identificativo de una notificaci�n de un Error*/
  public static final int EVENTO_ERROR = 6;


  /**
   * Constructor ClusterNetEventError
   * @param socket Un objeto SocketClusterNetImp
   * @param sInformativa cadena Informativa
   * @param evento El tipo de evento que se quiere crear
   */
  public ClusterNetEventError(SocketClusterNetImp socket,String sInformativa)
  {
    super(socket,EVENTO_ERROR,sInformativa);
  }


}

