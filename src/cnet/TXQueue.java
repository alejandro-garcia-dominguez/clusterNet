//============================================================
//
//	Copyright (c) 1999-2015 . All Rights Reserved.
//
//------------------------------------------------------------
//
//	File: TXQueue.java  1.0 24/11/99
//
//	Description: Clase TXQueue.
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

import java.util.LinkedList;
import java.util.Collections;
import java.util.List;

/**
 * TXQueue implementa una cola donde se almacenan los datos del
 * usurio en forma de objetos TPDUDatosNormal que encapsula los
 * datos y aptos para procesar y encolar en la Window de Emisi�n.<br>
 * Esta clase es thread-safe.
 * @version  1.0
 * @author M. Alejandro Garc�a Dom�nguez
 * <A HREF="mailto:alejandro.garcia.dominguez@gmail.com">(alejandro.garcia.dominguez@gmail.com)</A><p>
 *			   Antonio Berrocal Piris
 * @see Buffer
 */


class TXQueue
{

  /**
   * Lista enlazada para almacenar los objetos Buffer que encapsulan los
   * arrays de bytes
   */
  private LinkedList lista = null;

  /** La capacidad de la cola */
  private int iCapacidad = 0;

  /** El tama�o actual de la cola */
  private int iTama�o = 0;

 /**
   * Indica si los datos que vayan llegando a la cola de emisi�n ser�n
   * encolados o simplemente no se tendr�n en cuenta.
   */
  private boolean bEncolarDatos = true;

  /** Sem�for */
  private Semaforo semaforo = null;

  /** ClusterNet */
  private SocketClusterNetImp socket = null;

  /** Mutex */
  private Mutex mutex = null;

  /**
   * Flag de obtenciones. <br>
   * Utilizado para evitar situaciones de carrera entre un get y un remove.
   * Evita que se solapen buffers en uno en la funci�n add.
   */
  private boolean bGet = false;

  /**
   * Boolean bVacia. Indica si la cola est� vacia
   */
   private boolean bVacia = true;
  //==========================================================================
  /**
   * Constructor ColaEmisi�n
   * @param iCapacidad Capacidad de la cola en bytes.
   * @param socket SocketClusterNetImp
   */
  protected TXQueue( int iCapacidad,SocketClusterNetImp socket)
  {
    this.iCapacidad = iCapacidad;
    this.socket = socket;

    lista = new LinkedList();
    try
    {
      semaforo = new Semaforo(true,1);
      mutex = new Mutex();
    }
    catch(ClusterNetInvalidParameterException e)
    {
     Log.log("TXQueue --> ERROR FATAL",e.getMessage());
    }
   }


  //==========================================================================
  /**
   * Obtener la capacidad de la cola
   * @return un entero con la capacidad de la cola
   */
  int getCapacidad()
  {
    try
    {
      //Sincronizar threads...
      this.mutex.lock();

      return this.iCapacidad;
    }
    finally
    {

     //Sincronizar threads...
     this.mutex.unlock();
    }
  }

 //==========================================================================
  /**
   * Informa si la cola esta vacia
   * @return boolean true , la cola est� vacia, false en caso contrario
   */
  boolean  vacia()
  {
    return bVacia;
  }

  //==========================================================================
  /**
   * Obtener el tama�o de la cola
   * @return un entero con el tama�o de la cola
   */
  int  getTama�o()
  {
    try
    {
      //Sincronizar threads...
      this.mutex.lock();

     return this.iTama�o;
    }
    finally
    {
      //Sincronizar threads...
      this.mutex.unlock();
    }
  }

  //==========================================================================
  /**
   * Establece el valor de la variable encolarDatos.
   */
  void setEncolarDatos (boolean bEncolarDatosParam)
  {
      //Sincronizar threads...
      this.mutex.lock();

      this.bEncolarDatos = bEncolarDatosParam;

      //Sincronizar threads...
      this.mutex.unlock();

  }

  //==========================================================================
  /**
   * Verifica si se puede encolar datos en la cola.
   * @return true si la cola esta activa y false en caso contrario.
   */
  boolean esActiva(){ return this.bEncolarDatos; }



  //==========================================================================
  /**
   * Obtiene la lista interna de la cola.
   * @return LinkedList La lista enlazada.
   */
   LinkedList getLista()
   {
      return this.lista;
   }



  //==========================================================================
  /**
   * A�ade el RegistroBuffer pasado al principio de la cola de emisi�n.
   * Utilizado para casos en los que se borre de la cola y se tenga necesidad de
   * volver a a�adir.
   * @param buffer El objeto RegistroBuffer a insertar al proincipio de la lista.
   */
  void addFirst(RegistroBuffer buffer)
  {
    try
    {
      //Sincronizar threads...
      this.mutex.lock();

      this.lista.addFirst(buffer);
      this.iTama�o += buffer.getBuffer().getLength();
    }

    finally
    {
      if(this.iTama�o > 0)
        this.bVacia = false;
      else
        this.bVacia = true;

       //Sincronizar threads...
      this.mutex.unlock();

    }
  }


  //==========================================================================
  /**
   * A�adir un array de bytes al final de la cola.<br>
   * El array de bytes es encapsulado en un objeto Buffer y este es a�adido
   * a la cola.
   * Si el socket est� en modo FIABLE, si el �ltimo Buffer est� medio lleno,
   * este m�todo intenta llenarlo.
   * <b>Si no hay capacidad suficiente en el buffer el thread se queda bloqueado
   * hasta que se puedan a�adir los datos en la cola</b><br>
   * <b> EL ARRAY DE BYTES SE COPIA.</b>
   * @param array Un array de bytes.
   * @param array_offet Offset del array
   * @param array_length Longitud a copiar
   * @param bParamFin Bit de Fin de Transmisi�n
   * @return true si la operaci�n se ha realizado con �xito, false en caso contrario
   */
  private int iBytesAcumulados = 0;
  boolean add(byte[] array,int array_offset,int array_length,boolean bParamFin)
  {
    final String mn = "TXQueue.add";
    Buffer buf      = null;   // �ltimo Buffer en la cola
    Buffer datos    = null;   // Buffer de datos a a�adir a la cola
    boolean bFin = false;     //FIN TRANSMISION


    try
    {
      //Sincronizar threads...
      this.mutex.lock();

      // Tirar los datos si no se tienen que encolar.
      if (!this.bEncolarDatos)
      {
        return false;
      }


      //Comprobar que el array de bytes no es mayor que la capacidad de la cola
      if (array_length > this.iCapacidad)
      {
          throw new ClusterNetExcepcion("No se pudo insertar los datos en la cola. El dato que se quiere insertar es mayor que la capacidad de la cola.");
      }

      //PONER EL BIT DE FIN EN ESTE BUFFER.
      if(bParamFin)
      {
        bFin = true;
      }


       // Comprobar que hay capacidad en la cola
       while(this.iTama�o + array_length > this.iCapacidad ) //ANTES WHILE
       {
          //Sincronizar threads...
          this.mutex.unlock();

          //
          // No hay capacidad suficiente en el buffer, esperar a que la halla
          //
          // Log.log("COLA EMISION LLENA, DURMIENDO EL THREAD USUARIO","");
          this.semaforo.down();
          // Log.log("THREAD USUARIO DESPERTADO","");

          //Sincronizar threads...
          this.mutex.lock();
       }


       //Encapsular el array de bytes y !!!COPIARLO!!
       datos = new Buffer(array_length);
       datos.addBytes(new Buffer(array),array_offset,0,array_length);

       //A�adir a la cola
        if(!this.getLista().add(new RegistroBuffer(datos,bFin)))
        {
          return false; // No se ha encolado
        }

        // calcular el nuevo tama�o de la cola....
        this.iTama�o += (array_length);




      //ALEX: depuracion, comentar.
      // Log.log("====================================================","");
      // Log.log("TXQueue.add:",new String(datos.getBuffer()));
      // Log.log("====================================================","");


      return true; //OK. ENCOLADO.
     }
     catch(ClusterNetInvalidParameterException e)
     {
       Log.log(mn,e.getMessage());
       return false;
     }
     catch(ClusterNetExcepcion e)
     {
       Log.log(mn,e.getMessage());
       return false;
     }


     finally
     {
      if(this.iTama�o > 0)
        this.bVacia = false;
      else
        this.bVacia = true;

      //Sincronizar threads...
      this.mutex.unlock();
     }

  }




  //==========================================================================
  /**
   * A�adir un array de bytes al final de la cola.<br>
   * El array de bytes es encapsulado en un objeto Buffer y este es a�adido
   * a la cola.
   * Si el socket est� en modo FIABLE, si el �ltimo Buffer est� medio lleno,
   * este m�todo intenta llenarlo.
   * <b>Si no hay capacidad suficiente en el buffer el thread se queda bloqueado
   * hasta que se puedan a�adir los datos en la cola</b><br>
   * <b> EL ARRAY DE BYTES NO SE COPIA?.</b>
   * @param array Un array de bytes.
   * @param array_offet Offset del array
   * @param array_length Longitud a copiar
   * @param bParamFin Bit de Fin de Transmisi�n
   * @return true si la operaci�n se ha realizado con �xito, false en caso contrario
   */
   /*  2/05/2003 *****************  ANTIGUO ***************************

  private int iBytesAcumulados = 0;
  boolean add(byte[] array,int array_offset,int array_length,boolean bParamFin)
  {
    final String mn = "TXQueue.add";
    Buffer bufArray = null;   // Buffer para encapsular el array
    Buffer buf      = null;   // �ltimo Buffer en la cola
    Buffer datos    = null;   // Buffer de datos a a�adir a la cola
    int longNuevoBuffer  = 0; // Longitud del nuevo buffer que se a�adir� a la cola
    int longArray        = 0; //Longitud del array de bytes pasados
    int offset       =  0;    // offset del array
    boolean bFin = false;     //FIN TRANSMISION


    try
    {
      //Sincronizar threads...
      this.mutex.lock();

      //Log.log("COLA EMISION. ADD -> tama�o: "+array_length,"");
      //iBytesAcumulados+=array_length;
      //Log.log("COLA EMISION. Bytes Acumulados: "+iBytesAcumulados,"");

     //ALEX: depuracion, comentar.
     //  Log.log("====================================================","");
     //  Log.log("TXQueue.add:",new String(array,array_offset,array_length));
     //  Log.log("====================================================","");

      //Longitud del array;
      longArray        = array_length;

      // Tirar los datos si no se tienen que encolar.
      if (!this.bEncolarDatos)
      {
        return false;
      }

     /* if(this.getLista().size() != 0)
      {
        //
        // Obtener el �ltimo Buffer de la cola
        // Pol�tica--> CONSIDERAMOS "BUFFER NO LLENO" cuando el tama�o es
        //             menor de 3/4 del tama�o del Payload del TPDU
        //
        buf = (Buffer) this.lista.getLast();

        if((this.socket.getModo() == ClusterNet.MODE_RELIABLE || this.socket.getModo() == ClusterNet.MODE_SEMIRELIABLE )
        && (buf.getLength() < (ClusterNet.MTU *3)/4)
        && !bGet)
        {
              this.getLista().removeLast();

              //Encapsular el array de bytes y !!!COPIARLO!!
              bufArray = new Buffer(array_length + buf.getLength());
              buf.setOffset(0);
              bufArray.addBytes(buf,0,buf.getLength());
              bufArray.addBytes(new Buffer(array),array_offset,array_length);

              //Ajustar el tama�o...
              tama�o-=buf.getLength();

              longArray = bufArray.getLength();
        }
        else
        {
          //Encapsular el array de bytes y !!!COPIARLO!!
           bufArray = new Buffer(array_length);
           bufArray.addBytes(new Buffer(array),array_offset,0,array_length);
        }
      }
      else
        {
      */    //Encapsular el array de bytes y !!!COPIARLO!!
 /*         bufArray = new Buffer(array_length);
           bufArray.addBytes(new Buffer(array),array_offset,0,array_length);
      //  }


      while(offset < longArray)
      {

        //MODO FIABLE
        if(this.socket.getModo() == ClusterNet.MODE_RELIABLE || this.socket.getModo() == ClusterNet.MODE_SEMIRELIABLE)
        {
          //CALCULAR TAMA�O DEL NUEVO BUFFER
          // En Modo Fiable el tama�o m�ximo de un TPDU ser� el MTU.
          if( longArray - offset >  ClusterNet.MTU)
            longNuevoBuffer = ClusterNet.MTU; //Buffer m�ximo
          else
          {
            longNuevoBuffer = longArray - offset;

            //PONER EL BIT DE FIN EN ESTE BUFFER.
            if(bParamFin)
              bFin = true;
          }

        }
        else
        {

          //CALCULAR TAMA�O DEL NUEVO BUFFER
          //EN Modo NO FIABLE el tama�o m�ximo de un TPDU ser� TPDU_MAX_SIZE_PAYLOAD(64Kb)
          if( longArray - offset >  ClusterNet.TPDU_MAX_SIZE_PAYLOAD)
          {
            longNuevoBuffer = ClusterNet.TPDU_MAX_SIZE_PAYLOAD; //Buffer m�ximo

             //TIRAR LO QUE SOBRE SI PASA ClusterNet.TPDU_MAX_SIZE_PAYLOAD
              // Lo hacemos ajustando el offset..
              offset = longArray;
          }
          else
          {
            longNuevoBuffer = longArray - offset;
          }

          //PONER EL BIT DE FIN EN ESTE BUFFER.
          if(bParamFin)
              bFin = true;

        }


        // Comprobar que hay capacidad en la cola
        while(this.iTama�o + longNuevoBuffer > this.iCapacidad ) //ANTES WHILE
        {
          //Sincronizar threads...
          this.mutex.unlock();

          //
          // No hay capacidad suficiente en el buffer, esperar a que la halla
          //
          // Log.log("COLA EMISION LLENA, DURMIENDO EL THREAD USUARIO","");
          this.semaforo.down();
          // Log.log("THREAD USUARIO DESPERTADO","");

          //Sincronizar threads...
          this.mutex.lock();
        }


        // Crear el nuevo buffer
        datos = new Buffer(longNuevoBuffer);

        /*if(longUltimoBuffer != 0){
          datos.addBytes(buf,0,longUltimoBuffer);
          longUltimoBuffer = 0;
        }
        */

/*        datos.addBytes(bufArray,0,longNuevoBuffer);

        //A�adir a la cola
        if(!this.getLista().add(new RegistroBuffer(datos,bFin)))
          return false; // No se ha encolado

        // calcular el nuevo tama�o de la cola....
        this.iTama�o += (longNuevoBuffer);
        offset += longNuevoBuffer;

      }
      //      Log.log("COLA EMISION. ADD -> tama�o: "+this.tama�o,"");




      return true; //OK. ENCOLADO.
     }
     catch(ClusterNetInvalidParameterException e)
     {
       Log.log(mn,e.getMessage());
       return false;
     }
     catch(ClusterNetExcepcion e)
     {
       Log.log(mn,e.getMessage());
       return false;
     }


     finally
     {
      if(this.iTama�o > 0)
        this.bVacia = false;
      else
        this.bVacia = true;

      //Sincronizar threads...
      this.mutex.unlock();
     }

  }
*/
  //==========================================================================
  /**
   * Elimina un objeto RegistroBuffer de la cabeza de la cola.<br>
   * @return El objeto RegistroBuffer eliminado o null si no se ha eliminado ninguno
   */
  RegistroBuffer remove()
  {
    RegistroBuffer regBuf = null;

    try
    {
     //Sincronizar threads...
     this.mutex.lock();


      if(lista.size() != 0)
      {
        regBuf = (RegistroBuffer) lista.removeFirst();

        if(regBuf != null)
        {
          this.iTama�o -= regBuf.getBuffer().getLength();
          this.bGet = false;
        }

      }
        //      Log.log("COLA EMISION . REMOVE TAMA�O-->"+this.tama�o,"");
        //
        // Sem�foro arriba, despertar al thread de la aplicaci�n para ver si puede
        // encolar los datos...
        //
        this.semaforo.up();

      //ALEX: depuracion, comentar.
      //Log.log("====================================================","");
      //Log.log("TXQueue.remove:",new String(regBuf.getBuffer().getBuffer()));
      //Log.log("====================================================","");


      return regBuf;
    }

    finally
    {
      if(this.iTama�o > 0)
        this.bVacia = false;
      else
        this.bVacia = true;

      //Sincronizar threads...
      this.mutex.unlock();
    }
  }



  //==========================================================================
  /**
   * Establece la capacidad de la cola en bytes
   * @param la nueva capacidad de la cola de emisi�n
   */
  void setCapacidad(int capacidad)
  {
   try
   {
     //Sincronizar threads...
     this.mutex.lock();
       if(capacidad > 0)
       {
        if (capacidad < ClusterNet.TPDU_MAX_SIZE_PAYLOAD)
          this.iCapacidad = ClusterNet.TPDU_MAX_SIZE_PAYLOAD;
        else
         this.iCapacidad = capacidad;
       }



     //
     // Sem�foro arriba, despertar al thread de la aplicaci�n para ver si puede
     // encolar los datos...
     //
     this.semaforo.up();

   }

   finally
   {
      //Sincronizar threads...
      this.mutex.unlock();
   }

  }

}
