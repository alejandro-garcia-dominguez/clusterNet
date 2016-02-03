/**
  Fichero: ProtocolcFTP.java  1.0 1/12/99
  Copyright (c) 2000-2014 . All Rights Reserved.
  Autor: Alejandro Garc�a Dom�nguez alejandro.garcia.dominguez@gmail.com   alejandro@iacobus.com
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
package org.clusternet.ftp;

import java.io.*;

import javax.swing.JOptionPane;

import org.clusternet.*;
import javax.swing.Icon;

import java.util.TreeMap;
import java.util.Iterator;

/**
 * Protocolo FTP Multicast versi�n 1.0
 */
public class ProtocolcFTP extends Thread
  implements ClusterNetRxDataListener, ClusterNetMemberInputStreamListener
    ,ClusterNetConnectionListener, ClusterNetGroupListener, ClusterNetMemberListener
{

   /** MAGIC */
  public final static int MAGIC = 0x6DED757B;

  /** VERSION 1*/
  public final static int VERSION = 0x01;

  /** Tama�o del array de Transmisi�n/Recepcion */
  public static final int TAMA�O_ARRAY_BYTES = 1024 * 2;

  /** Fichero */
  File file = null;

  /** Flujo de salida del Fichero */
  private FileOutputStream fileOutputStream = null;

  /** TAma�o del Fichero */
  long lFileSize = 0;

  /** Nombre del Fichero*/
  String sFileName = null;

  /** Flag de lectura de datos del socket */
  private boolean bLeer = false;

  /** Sem�foro binario de ESPERA*/
  private Semaforo semaforoFin = null;

  /** Sem�foro binario para EMISION*/
  private Semaforo semaforoEmision = null;

  /** Sem�foro binario para RECEPCION*/
  private Semaforo semaforoRecepcion = null;

    /** Flag de parada de la transferencia */
  private boolean bStop = false;

  /** Flujo de salida */
  private ClusterNetOutputStream out = null;

  /** Flujo de entrada Multicast*/
  private ClusterNetInputStream inMcast = null;

  /** Nueva L�nea */
  private static final String newline = "\n";

  /** Address dirIPMcast */
  private Address dirIPMcast = null;

  /** Address dirIPInterfaz */
  private Address dirIPInterfaz = null;

  /** clave */
  private char[] clave = null;

  /**TTL sesion */
  private int TTLSesion = 0;

  /** Registro_ID_Socket */
  private RegistroID_Socket_Buffer reg = null;

  /** Icono del fichero enviado*/
  private Icon icon = null;

  /**
   * TreeMap de Threads ThreadRecepcion. KEY= ID_SocketInputStream. VALUE=FileRecepcion
   *  UTILIZADO EN MODO FIABLE.
   */
  private TreeMap treemapID_SocketInputStream = null;

  /**
   * TreeMap de ID_SOCKETS. KEY= ID_SOCKET. VALUE= Filerecepcion
   * UTILIZADO EN MODO NO_FIABLE.
   */
  private TreeMap treemapID_Socket = null;

  /** Socket Multicast Fiable*/
  private ClusterNet socket = null;

  /** Modo de fiabilidad del socket*/
  private int modo = 0;

  /** Socket Multicast No Fiable*/
  private ClusterNet datagramSocket = null;

  /** Flag de inicio del thread */
  private boolean runFlag = true;

  /** Ratio ed transferencia */
  private long lRatio = 0;


 //==========================================================================
 /**
  * Constructor
  */
  public ProtocolcFTP() throws IOException
  {
    super("ProtocolcFTP");

    setDaemon(true);

    try
    {
      //Crear sem�foros
      semaforoFin = new Semaforo(true,1);
      semaforoEmision = new Semaforo(true,1);
      semaforoRecepcion = new Semaforo(true,1);
    }
    catch(ClusterNetInvalidParameterException e){;}

  }


 //==========================================================================
 /**
  * M�todo Run()
  */
 public void run()
 {
   Cipher cipher = null;
   boolean bCipher = false;
   cFtp ftp = cFtp.getFTP();
   try
   {
     runFlag = true;

       // Log.log("CRIPTOGRAFIA--> CLAVE:"+clave ,"");

     if(!(new String(clave).equals("")))
     {
        Log.log("CRIPTOGRAFIA--> CLAVE:"+clave ,"");
        cipher = Cipher.getInstance( clave);

        if(cipher == null)
        {
          cFtp.getFTP().error("No se ha podido crear los objetos de Cifrado.");
          return;
        }
        else
         bCipher = true;
     }

    //Establecer nivel de depuracion
    ClusterNet.setLogLevel(Log.ACK | Log.HACK | Log.NACK | Log.HNACK | Log.HSACK | Log.TPDU_RTX);


     //1.- Crear el socket..
     if (modo == ClusterNet.MODE_DELAYED_RELIABLE || modo == ClusterNet.MODE_RELIABLE)
     {
        //Log.log("MODO ClusterNet ClusterNet FIABLE /RETRASADO","");
       //Iniciar Logo...
        cFtp.getFTP().logoOn();
       if(bCipher)
         socket = new ClusterNet(dirIPMcast,dirIPInterfaz,(byte)TTLSesion,modo,this,cipher.getCipher(),cipher.getUncipher());
       else
         socket = new ClusterNet(dirIPMcast,dirIPInterfaz,(byte)TTLSesion,modo,this);


       //Registrar listener eventos...
       //this.socket.addPTMFConexionListener(this);
       this.socket.addGroupListener(this);
       this.socket.addMemberListener(this);

       //Obtener idgls e id_scoket
       ftp.idgls = this.socket.getNumGroups();
       ftp.id_sockets = this.socket.getNumMembers();
       //ftp.getJLabelIDGLs().setText("IDGLS: "+ftp.idgls);
       //ftp.getJLabelID_Sockets().setText("ID_Sockets: "+ftp.id_sockets);

       //Obtener los IDGLs conocidos...
       TreeMap treemapIDGL = this.socket.getGroups();
       Iterator iterator = treemapIDGL.values().iterator();

       while(iterator.hasNext())
       {
          RegistroIDGL_TreeMap regIDGL = (RegistroIDGL_TreeMap) iterator.next();

          ftp.getJFrame().jTreeInformacion.addIDGL(regIDGL.getIDGL());
       }

       //Obtener los IDGLs conocidos...
       TreeMap treemapIDSocket = this.socket.getMembers();
       Iterator iteratorSockets = treemapIDSocket.keySet().iterator();

       while(iteratorSockets.hasNext())
       {
          ID_Socket idSocket = (ID_Socket) iteratorSockets.next();

          ftp.getJFrame().jTreeInformacion.addID_Socket(idSocket);
       }


       //Obtener Flujos de Entrada y de Salida
       this.out = this.getSocket().getClusterOutputStream();
       this.inMcast = this.getSocket().getClusterInputStream();

       if(runFlag==false)
         return;

     }
     else
     {
       //Iniciar Logo...
        ftp.logoOn();

       if(bCipher)
         datagramSocket = new ClusterNet(dirIPMcast,dirIPInterfaz,(byte)TTLSesion,modo,this,cipher.getCipher(),cipher.getUncipher());
       else
         datagramSocket = new ClusterNet(dirIPMcast,dirIPInterfaz,(byte)TTLSesion,modo,this,null,null);

       if(runFlag==false)
          return;

       //Registrar listeners eventos...
       datagramSocket.addConnectionListener(this);

     }

     if(runFlag==false)
        return;

     //Conectado���
     ftp.insertInformacionString("Conexi�n Multicast establecida con "+dirIPMcast+ "TTL= "+TTLSesion);

     if(runFlag==false)
        return;


     // ENVIAR/RECIBIR FICHEROS...
     if( ftp.esEmisor())
     {

        this.socket.setRatioTx(lRatio);
        ftp.insertInformacionString("Ratio de transferencia: "+lRatio/1024+" KB/Seg");


        //Cerrar RECEPCION����
        if(socket!=null)
          socket.disableRx();

        //Cerrar RECEPCION����
        if(datagramSocket!=null)
          datagramSocket.disableRx();
        
        ftp.getJFrame().jPanelTransmisor.setEnabled(true);
        ftp.getJFrame().jPanelReceptor.setEnabled(false);

        this.waitSendFiles();
     }
     else
     {
        ftp.getJFrame().jPanelTransmisor.setEnabled(false);
        ftp.getJFrame().jPanelReceptor.setEnabled(true);

        this.waitReceiveFiles();
     }

     return;
  }
  catch(ClusterNetInvalidParameterException e)
  {
     ftp.error(e.getMessage());
  }
  catch(ClusterNetExcepcion e)
  {
     ftp.error(e.getMessage());
  }
  catch(IOException e)
  {
     ftp.error(e.getMessage());
  }


  //Limpiar....
  finally
  {
      //Cerrar el Socket
      close();
      Log.log("FIN ProtocolcFTP","");
  }
 }

 //==========================================================================
 /**
  * conectar
  */
  public void conectar(Address dirIPMcast,
      Address dirIPInterfaz,int TTLSesion,long lRatio, int modo, char[] clave)
  {
    this.dirIPMcast = dirIPMcast;
    this.dirIPInterfaz = dirIPInterfaz;
    this.modo = modo;
    this.TTLSesion = TTLSesion;
    this.clave = clave;
    this.lRatio = lRatio;

    // Iniciar el thread
    this.start();
 }

 //==========================================================================
 /**
  * getSocket()
  */
 ClusterNet getSocket(){ return this.socket;}

 //==========================================================================
 /**
  * getDatagramSocket()
  */
 ClusterNet getDatagramSocket(){ return this.datagramSocket;}

 //==========================================================================
 /**
  * getFTP()
  */
 cFtp getFTP(){ return cFtp.getFTP();}

  //==========================================================================
 /**
  * getModo()
  */
 int getModo(){ return this.modo;}

 //==========================================================================
 /**
  * getMulticastOutputStream()
  */
 //ClusterNetOutputStream getMulticastOutputStream(){ return this.out;}

 //==========================================================================
 /**
  * Cerrar el Socket
  */
 void close()
 {
   try
    {
      //Cerrar el Socket...
      if (modo  == ClusterNet.MODE_DELAYED_RELIABLE || modo  == ClusterNet.MODE_RELIABLE)
        {
         if(socket!= null)
         {
            socket.endTx();
            socket.close(ClusterNet.CLOSE_STABLE);
         }
        }
      else
        if(datagramSocket!= null)
            datagramSocket.close();

         //if(semaforoFin == null)

    // ?�?�?�?�??�?� ThreadRecepcion.interrupted();*******************-----

     //Despertar...
     if (semaforoFin != null)
       semaforoFin.up();

     if(semaforoEmision != null)
       semaforoEmision.up();

     if(semaforoRecepcion != null)
       semaforoEmision.up();


    }
    catch(ClusterNetExcepcion e)
    {
            finTransferencia(e);
    }
 }

 //==========================================================================
 /**
  * FinTransferencia
  */
  void finTransferencia(IOException ioe)
  {
      cFtp ftp = cFtp.getFTP();

      ftp.insertStringJTextPane(ftp.getJTextPaneInformacion(),ioe.getMessage(),"error");
      ftp.insertInformacionString("Conexi�n Cerrada");
      cFtp.getFTP().logoOff();
      this.runFlag = false;
  }

 //==========================================================================
 /**
  * M�todo stopThread()
  */
 public void stopThread()
 {
   this.runFlag = false;

   if(semaforoRecepcion!= null)
     semaforoRecepcion.up();

     if (semaforoFin != null)
       semaforoFin.up();

     if(semaforoEmision != null)
       semaforoEmision.up();

     if(semaforoRecepcion != null)
       semaforoEmision.up();


  //if( this.protocoloFTPMulticast!= null)
  // this.protocoloFTPMulticast.close();
 }

 //==========================================================================
 /**
  * Indica si la sesi�n est� activa o desactivada.
  * @return true si la sesi�n est� activa, false en caso contrario
  */
 public boolean esActiva()
 {
   return this.runFlag;
 }



 //==========================================================================
 /**
  * Implementaci�n de la interfaz ClusterNetConnectionListener
  */
 public void actionNewConnection(ClusterNetEventConecction evento)
 {
    cFtp ftp = cFtp.getFTP();
    //Log.log("actionPTMFConexion","");
    //Log.log("actionPTMFConexion: "+evento.getString(),"");

    if( ftp != null && runFlag==true)
    {
       ftp.insertInformacionString(evento.getString());
       //ftp.insertStringJTextPane(" ","icono_informacion");
       //ftp.insertInformacionString(evento.getString());
    }
 }

 //==========================================================================

 //==========================================================================
 /**
  * Espera para la emisi�n de ficheros...
  */
 void waitSendFiles() throws IOException
 {
         //BUCLE PRINCIPAL
     while(this.esActiva())
     {
          //Si NO HAY NADA QUE EMITIR--> DORMIR HASTA QUE LO HAYA.
          if(file== null || this.bStop == true)
           this.semaforoEmision.down();

          //Verificar si se ha cerrado la conexi�n...
          if(!this.esActiva())
          {
            limpiar();
            return;
          }

          //Enviar fichero....
          FileEmision fileEmision = new FileEmision(this,this.file,this.icon);
          fileEmision.sendFile();

          if(!this.esActiva())
          {
            limpiar();
            return;
          }

          this.file = null;
   }
 }

 //==========================================================================
 /**
  * Espera para la recepci�n de ficheros...
  */
 void waitReceiveFiles() throws IOException
 {
    if(this.getModo() == ClusterNet.MODE_DELAYED_RELIABLE || this.getModo()  == ClusterNet.MODE_RELIABLE)
    {
      //Registrar ID_SocketInputStreamListener
      this.getSocket().getClusterInputStream().addPTMFID_SocketInputStreamListener(this);

    }
    else
    {
      //Registrar PTMFDatosRecibidos
      this.getDatagramSocket().addRxDataListener(this);

      //Crear el Treemap si es NULL
      if(this.treemapID_Socket == null)
       this.treemapID_Socket = new TreeMap();
    }

    //Informaci�n..
    cFtp.getFTP().insertInformacionString("Esperando recepci�n de ficheros...");
    cFtp.getFTP().insertRecepcionString("Esperando recepci�n de ficheros...","icono_informacion");

    //***** BUCLE PRINCIPAL *****
    while(this.esActiva())
    {

      if(this.getModo() != ClusterNet.MODE_DELAYED_RELIABLE && this.getModo()  != ClusterNet.MODE_RELIABLE)
      {
        //MODO NO-FIABLE
        //ESPERAR A QUE HAYA DATOS..
        this.semaforoRecepcion.down();

        //Leer Bytes NO FIABLE...
        recibirDatagrama();
      }
      else
      { //MODO FIABLE
        //ESPERAR A QUE FINALICE EL THREAD SESION MULTICAST,
        // LA RECEPCION SE HACE DE FORMA AS�NCRONA CON EL LISTENER ID_SOCKETINPUTSTREAM...
        while(this.esActiva())
          ClusterTimer.sleep(500);
      }
    }//FIN WHILE PRINCIPAL
 }


 //==========================================================================
 /**
  * recibirDatagrama();
  */
 private void recibirDatagrama() throws IOException
 {
   byte[] bytes = new byte[this.TAMA�O_ARRAY_BYTES];
   String sFileName = null;
   long lFileSize = 0;

   //1.- **Leer DATOS**
   RegistroID_Socket_Buffer reg = this.getDatagramSocket().receive();

   //Crear el Treemap si es NULL
   if(this.treemapID_Socket == null)
    this.treemapID_Socket = new TreeMap();


   // SI EL ID_Socket no est� en el treemap, Significa CONEXI�N NUEVA....
   if(!this.treemapID_Socket.containsKey(reg.getID_Socket()))
   {
       Log.log("NUEVO ID_Socket: "+reg.getID_Socket(),"");
       Buffer buf = reg.getBuffer();

       //Comprobar IDFTP
       if (!FileRecepcion.parseIDFTPMulticast(buf))
        return;

       //Comprobar Tama�o
       lFileSize = FileRecepcion.parseFileSize(buf);
       if(lFileSize <= 0)
        return;

       //Comprobar FileName
       sFileName = FileRecepcion.parseFileName(buf);
       if( sFileName == null)
        return;

       // protocoloFTPMulticast.getFTP().insertStringJTextPane(" ","icono_entrada");
       cFtp.getFTP().insertRecepcionString("Iniciando la recepci�n... de "+sFileName,null);

       //this.getFTP().insertStringJTextPane(" ","icono_entrada");
       cFtp.getFTP().insertRecepcionString("Recibiendo fichero: "+sFileName+" del emisor: "+reg.getID_Socket(),null);
       //this.getFTP().insertStringJTextPane(" ","icono_entrada");
       cFtp.getFTP().insertRecepcionString("Tama�o: "+lFileSize,null);


       //Nuevo FileRecepcion...
       FileRecepcion fileRecepcion  = new FileRecepcion(this,reg.getID_Socket());
       //Recibir fichero
       fileRecepcion.receiveFile(lFileSize,sFileName);

       //A�adir nuevo FileRecepcion al treemap...
       this.treemapID_Socket.put(reg.getID_Socket(),fileRecepcion);

   }
   else
   {
      //Obtener FileRecepcion...
      FileRecepcion fileRecepcion = (FileRecepcion) this.treemapID_Socket.get(reg.getID_Socket());

      //A�adir los bytes le�dos...
      fileRecepcion.addBytes(reg.getBuffer().getBuffer(),reg.getBuffer().getLength());

      //FALTA FALTA FALTA FALTA FALTA FALTA FALTA FALTA FALTA FALTA
      //FALTA FALTA FALTA FALTA FALTA FALTA FALTA FALTA FALTA FALTA
      //FALTA FALTA FALTA FALTA FALTA FALTA FALTA FALTA

      //ALES FALYA CONTENPLAR FIN DE FLUJO �?�?��?�????�?�? *****************---------

      //  FALTA FALTA FALTA FALTA FALTA FALTA FALTA FALTA FALTA
      //FALTA FALTA FALTA FALTA FALTA FALTA FALTA FALTA FALTA FALTA

   }

 }


 //==========================================================================
 /**
  * Eliminar un FileRecepcion. MODO NO FIABLE.
  * @param ID_Socket
  */
 void removeFileRecepcion(ID_Socket id_socket)
 {
    this.treemapID_Socket.remove(id_socket);
 }

 //==========================================================================
 /**
  * Eliminar un FileRecepcion. MODO FIABLE.
  */
  void removeFileRecepcion(ID_SocketInputStream idIn)
  {
    //Log.log("Remove fileRecepcion: "+idIn,"");

    if (idIn == null)
      Log.log("idIN ES NULLLLL����","");
    if (this.treemapID_SocketInputStream!= null)
      this.treemapID_SocketInputStream.remove(idIn);
  }


 //==========================================================================
 /**
  * Mensaje de advertencia--> El Fichero Existe. Petici�n de sobreescribir.
  * @return true si se quiere sobreescribir, false en caso contrario.
  */
 private boolean mensajeFileExists()
 {
  boolean b = false;
  try
  {
    int iOpcion =  JOptionPane.showConfirmDialog(null,"El fichero "+sFileName+newline+"ya existe. �Desea sobreescribir el fichero existente?",
				    "Sobreescribir", JOptionPane.YES_NO_OPTION);
    if(iOpcion == JOptionPane.YES_OPTION)
      b = true;


  }
  finally
  {
   return b;
  }
 }

 //==========================================================================
 /**
  * Mensaje de advertencia--> No se puede escribir en el fichero.
  */
 private void mensajeErrorEscritura()
 {
    JOptionPane.showMessageDialog(null,"No se puede escribir en el fichero: "+sFileName+newline+"no se tiene permiso de escritura"+newline+"Verifique los permisos de escritura"+newline+" y que tiene suficiente privilegio para escribir." ,
				    "Error Escritura",JOptionPane.ERROR_MESSAGE);
 }

 //==========================================================================
 /**
  * Mensaje de advertencia--> Error Escribiendo
  */
 private void mensajeErrorEscribiendo(String sError)
 {
    JOptionPane.showMessageDialog(null,"Se ha producido un error mientras se intentaba escribir en el fichero"+newline+sFileName+newline+"El error es el siguiente:"+sError ,
				    "Error Escritura",JOptionPane.ERROR_MESSAGE);
 }





//==========================================================================
 /**
  * sendFile env�a el fichero sFile por el canal Multicast.
  * @param file el fichero que se desea transmitir por Multicast
  * @param icon Icono representativo del fichero
  * @return Boolean. true si se ha iniciado la transferencia, false en caso contrario.
  */
 public boolean sendFile(File file,Icon icon)
 {
    if (!esActiva())
      return false;

    if(this.file!=null)
    {
      errorFile("Ya hay una transferencia en curso.\nPor favor, espere a que termine para poder iniciar otra.");
      return false;
    }

    //Asignar..
    this.file = file;
    this.icon = icon;

    //Enviar...
    this.bStop = false;
    this.semaforoEmision.up();
    return true;
 }




 //==========================================================================
 /**
  * Implementaci�n de la interfaz ClusterNetGroupListener
  * para la recepci�n de datos en modo NO_FIABLE
  */
 public void actionPTMFIDGL(ClusterNetEventGroup evento)
 {
    cFtp ftp = cFtp.getFTP();

    if( evento.esA�adido())
    {
       ftp.idgls = this.socket.getNumGroups();

       //A�adir el IDGL al �rbol de informaci�n
       ftp.getJFrame().jTreeInformacion.addIDGL(evento.getIDGL());

       cFtp.getFTP().insertInformacionString("IDGLS: "+cFtp.getFTP().idgls);
       cFtp.getFTP().insertInformacionString("Nuevo IDGL: "+evento.getIDGL());

    }
    else
    {
      ftp.idgls = this.socket.getNumGroups();

      //Eliminar IDGLs del �rbol
      ftp.getJFrame().jTreeInformacion.removeIDGL(evento.getIDGL());

       cFtp.getFTP().insertInformacionString("IDGLS: "+cFtp.getFTP().idgls);
       cFtp.getFTP().insertInformacionString("IDGL eliminado: "+evento.getIDGL());
    }
 }

  //==========================================================================
 /**
  * Implementaci�n de la interfaz ClusterNetGroupListener
  * para la recepci�n de datos en modo NO_FIABLE
  */
 public void actionID_Socket(ClusterNetEventMember evento)
 {
    cFtp ftp = cFtp.getFTP();

    if( evento.esA�adido())
    {
      ftp.id_sockets = this.socket.getNumMembers();

      //A�adir el ID_Socket al �rbol de informaci�n
      ftp.getJFrame().jTreeInformacion.addID_Socket(evento.getID_Socket());

       cFtp.getFTP().insertInformacionString("ID_Sockets: "+cFtp.getFTP().id_sockets);
       cFtp.getFTP().insertInformacionString("Nuevo ID_Socket: "+evento.getID_Socket());
    }
    else
    {
      ftp.id_sockets = this.socket.getNumMembers();

      //A�adir el ID_Socket al �rbol de informaci�n
      ftp.getJFrame().jTreeInformacion.removeIDSocket(evento.getID_Socket());

      ftp.insertInformacionString("ID_Sockets: "+cFtp.getFTP().id_sockets);
      ftp.insertInformacionString("ID_Socket eliminado: "+evento.getID_Socket());
    }

 }

 //==========================================================================
 /**
  * Implementaci�n de la interfaz ClusterNetRxDataListener
  * para la recepci�n de datos en modo NO_FIABLE
  */
 public void actionPTMFDatosRecibidos(ClusterNetEventNewData evento)
 {
    // Hay datos, despertar si estaba dormido
    this.bLeer = true;
    this.semaforoRecepcion.up();
 }



 //==========================================================================
 /**
  * ClusterNetEventMemberInputStream
  */
 public void actionPTMFID_SocketInputStream(ClusterNetEventMemberInputStream evento)
 {
   //Log.log("\n\nNUEVO ID_SOCKETINPUTSTREAM","");

   //Crear TreeMap threads de recepcion ...
   if ( treemapID_SocketInputStream == null)
      this.treemapID_SocketInputStream = new TreeMap();

   ID_SocketInputStream idIn = evento.getID_SocketInputStream();
   if (idIn == null)
   {
    //Log.log("\n\nNUEVO ID_SOCKETINPUTSTREAM: NULL","");
    return;
   }

   //Log.log("\n\n ID_SOCKETINPUTSTREAM: --->OK","");

   if( !this.treemapID_SocketInputStream.containsKey(idIn))
   {
        // Log.log("\n\n PARA CREAR THREADS","");

       try
       {
          //PONER --> Obtener lFilesize y sFileName antes de crear FileRecepcion
          FileRecepcion fileRecepcion = new FileRecepcion(this,idIn);
          //Log.log("\n\nCREANDO THREAD Filerecepcion","");

          this.treemapID_SocketInputStream.put(idIn,fileRecepcion);

          //Iniciar thread...
          fileRecepcion.start();
       }
       catch(IOException ioe)
       {
          cFtp.getFTP().error(ioe.toString());
       }
   }
 }
 //==========================================================================
 /**
  * Error Abriendo el Fichero.
  * @param sCadenaInformativa
  */
 private void errorFile(String sCadenaInformativa)
 {
   JOptionPane.showMessageDialog(null,sCadenaInformativa,
				    "Error", JOptionPane.ERROR_MESSAGE);

 }





 //==========================================================================
  /**
   *  Limpiar variables....
   */
  private void limpiar()
  {
     this.file = null;
     this.bStop = true;
  }




}