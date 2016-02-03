//============================================================
//
//	Copyright (c) 1999 - 2015 . All Rights Reserved.
//
//------------------------------------------------------------
//
//	Fichero: ClusterNetErrorListener.java  1.0 24/9/99
//

//	Descripci�n: Clase ClusterNetErrorListener.
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

import java.io.*;
import java.util.*;


/**
 * Interfaz ClusterNetErrorListener.
 * Notificaci�n de mensajes de ERROR por parte del socket ClusterNet
 * a la aplicaci�n .<br>
 * <B>LOS EVENTOS DEBEN DE SER TRATADOS CON LA MAYOR BREVEDAD, PARA
 * NO AFECTAR AL RENDIMIENTO DE ClusterNet<B>. Por ejemplo, se deben de usar
 * los eventos para establecer banderas cuya acci�n sea realizada por
 * un thread aparte.
 * @version  1.0
 * @author M. Alejandro Garc�a Dom�nguez
 * <A HREF="mailto:alejandro.garcia.dominguez@gmail.com">(alejandro.garcia.dominguez@gmail.com)</A><p>
 *			   Antonio Berrocal Piris
 */

public interface ClusterNetErrorListener
{
  //==========================================================================
  /**
   * El m�todo callback
   * @param evento ClusterNetEventError
   */
  public void actionError(ClusterNetEventError evento);

}


