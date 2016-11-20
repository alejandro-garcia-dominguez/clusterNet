/**
  Fichero: ThreadSesionMulticast.java  1.0 1/12/99
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

package cnet.mcopy;

import java.io.*;

import javax.swing.JOptionPane;

import cnet.*;

import javax.swing.Icon;

import java.util.TreeMap;

 //==========================================================================
 /**
  * Clase ThreadSesionMulticast. Gestiona la conexi�n Multicast.
  */
 public class ThreadSesionMulticast extends  Thread  implements  ClusterNetConnectionListener
 {

	@Override
	public void actionNewConnection(ClusterNetEventConnection evento) {
		// TODO Auto-generated method stub
		
	}

 
}
