//============================================================================
//
//	Copyright (c) 1999-2015. All Rights Reserved.
//
//----------------------------------------------------------------------------
//
//	Fichero: TPDUDatosNormal.java  1.0 9/9/99
//
//
//	Descripci�n: Clase TPDUDatosNormal.
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

package cnet;

import java.util.TreeMap;

import java.util.Vector;

/**
 * Clase TPDU Datos Normal.<br>
 * Hereda de la clase TPDUDatos.<br>
 *
 * Para crear un objeto de esta clase se tienen que usar los m�todos est�ticos.
 * Una vez creado no puede ser modificado.<br>
 *
 * El formato completo del TPDU Datos Normal es: <br>
 *
 *                      1 1 1 1 1 1 1 1 1 1 2 2 2 2 2 2 2 2 2 2 3 3<br>
 * +0-1-2-3-4-5-6-7-8-9-0-1-2-3-4-5-6-7-8-9-0-1-2-3-4-5-6-7-8-9-0-1<br>
 * +---------------------------------------------------------------+<br>
 * +      Puerto Multicast         |          Puerto Unicast       +<br>
 * +---------------------------------------------------------------+<br>
 * +                    ID_GRUPO_LOCAL(4 bytes primeros)           +<br>
 * +---------------------------------------------------------------+<br>
 * +ID_GRUPO_LOCAL(2 bytes �ltimos)|          Longitud             +<br>
 * +---------------------------------------------------------------+<br>
 * +                               |   | | | | | |A|I|F|F|         +<br>
 * +           Cheksum             | V |0|1|0|0|0|C|R|I|I| / / / / +<br>
 * +                               |   | | | | | |K| |N|N|         +<br>
 * +                               |   | | | | | |K| |C|T|         +<br>
 * +---------------------------------------------------------------+<br>
 * +        Tama�o Window         |           N�mero R�faga       +<br>
 * +---------------------------------------------------------------+<br>
 * +                      NUMERO DE SECUENCIA                      +<br>
 * +---------------------------------------------------------------+<br>
 * +                           Datos   ...                         +<br>
 * +---------------------------------------------------------------+<br>
 * <br>
 * <br>
 * Esta clase no es thread-safe.<br>
 * @see      Buffer
 * @version  1.0
 * @author M. Alejandro Garc�a Dom�nguez
 * <A HREF="mailto:Malejandro.garcia.dominguez@gmail.com">(Malejandro.garcia.dominguez@gmail.com)</A><p>
 * Antonio Berrocal Piris
 * <A HREF="mailto:AntonioBP.wanadoo.es">(AntonioBP@wanadoo.es)</A><p>
 */
public class TPDUDatosNormal extends TPDUDatos
{
  // ATRIBUTOS
  /** Longitud de cabecera com�n a todos los TPDUDatosNormal */
  static final int LONGHEADER =  5*4  + 4;

  /**
   * IR (1 bit): Inicio de r�faga
   */
  byte IR = 0;

  /**
   * FIN_CONEXION (1 bit): Fin de la conexi�n
   */
  byte FIN_CONEXION = 0;

  /**
   * FIN_TRANSMISION (1 bit): Fin de la transmisi�n
   */
  byte FIN_TRANSMISION = 0;

  /**
   * ACK (1 bit):
   */
  byte ACK = 0;

  /**
   * N�mero de R�faga (16 bits): N�mero de r�faga al que pertenece el TPDU.
   */

  int NUMERO_RAFAGA = 0;


  /**

   * Tama�o de ventana (16 bits):

   */

  int TAMA�O_VENTANA = 0;


  /** El n�mero de secuencia de este TPDU de datos Normal. */
  SecuenceNumber NUMERO_SECUENCIA = null;

  /** Datos */
  Buffer BUFFERDATOS = null;

  /** ID TPDU : utilizado por la funci�n {@link #getID_TPDU()}*/
  ID_TPDU ID_TPDU_FUENTE = null;

  //==========================================================================
  /**
   * Constructor utilizado para crear un TPDUDatosNormal.
   * @param socketClusterNetImp Objeto SocketClusterNetImp del que obtiene el valor de los
   * campos de la cabecera com�n.
   * @exception ClusterNetExcepcion
   * @exception ClusterNetInvalidParameterException lanzada si socketClusterNetImp es null
   */
  private TPDUDatosNormal (SocketClusterNetImp socketClusterNetImp)
    throws ClusterNetExcepcion,ClusterNetInvalidParameterException
  {
   super (socketClusterNetImp);
  }

 //============================================================================
 /**
  * Crea un TPDUDatosNormal con la informaci�n facilitada.
  * @param socketClusterNetImp Objeto SocketClusterNetImp del que obtiene el valor de los
  * campos de la cabecera com�n.
  * @param puertoMulticast
  * @param puertoUnicast
  * @param clusterGroupID
  * @param dirIp direcci�n IP unicast del emisor del TPDUDatosNormal
  * @param puertoUnicastFuente puerto unicast fuente
  */
  private TPDUDatosNormal (int puertoMulticast,
                           int puertoUnicast,
                           ClusterGroupID clusterGroupID,
                           IPv4 dirIp)
    throws ClusterNetExcepcion,ClusterNetInvalidParameterException
  {
   super (puertoMulticast,puertoUnicast,clusterGroupID,dirIp);
  }

  //==========================================================================

  /**
   * Constructor por defecto.
   * Este constructor es para crear TPDUS a partir del parser de un Buffer
   * @exception ClusterNetExcepcion
   * @exception ParametroInvalido Se lanza en el constructor de la clase TPDU.
   */
  private TPDUDatosNormal ()

      throws ClusterNetInvalidParameterException,ClusterNetExcepcion

  {

   super();

  }



 //============================================================================

 /**
  * Crea un vector con TPDUDatosRtx. que contiene este TPDUDatosNormal y las
  * listas de no asentidos. El vector contendr� uno o m�s TPDUDatosRtx.
  * @param socketPTMFImpParam utilzado para obtener informaci�n de la cabecera
  * com�n a todos los TPDU.
  * @param treeMapID_Socket contiene los id_sockets que no han enviado ACK
  * para el TPDU Datos Rtx.
  * @param treeMapIDGL contiene los idgls que no han enviado HACK o HSACK
  * para el TPDU Datos Rtx.
  * @return vector con los TPDUDatosRtx formados
  * @exception ClusterNetInvalidParameterException si alguno de los par�metros es err�neo.
  * @exception ClusterNetExcepcion si hay un error al crear los TPDUDatosRtx a partir
  * de la informaci�n facilitada en los argumentos.
  * @see TPDUDatosRtx
  */

 Vector convertirAVectorTPDUDatosRtx (SocketClusterNetImp socketPTMFImpParam,
                                      TreeMap treeMapID_Socket,
                                      TreeMap treeMapIDGL)
                             throws ClusterNetInvalidParameterException, ClusterNetExcepcion
 {

  return TPDUDatosRtx.crearVectorTPDUDatosRtx (socketPTMFImpParam,
                                         ((this.IR  == 1) ? true : false),
                                         ((this.ACK == 1) ? true : false),
                                         ((this.FIN_CONEXION == 1) ? true : false),
                                         ((this.FIN_TRANSMISION == 1) ? true : false),
                                         this.NUMERO_RAFAGA,
                                         this.ID_GRUPO_LOCAL,
                                         this.ID_TPDU_FUENTE,
                                         treeMapID_Socket,
                                         treeMapIDGL,
                                         this.BUFFERDATOS);
 }


 //============================================================================
 /**
  * Crea un TPDUDatosNormal con la informaci�n facilitada.
  * @param socketClusterNetImp Objeto SocketClusterNetImp del que obtiene el valor de los
  * campos de la cabecera com�n.
  * @param setIR
  * @param setACK
  * @param setFIN_CONEXION
  * @param setFIN_TRANSMISION
  * @param numeroRafaga
  * @param nSec n�mero de secuencia
  * @param datos
  * @exception ClusterNetInvalidParameterException si alguno de los par�metros es err�neo.
  * @exception ClusterNetExcepcion si hay un error al crear el TPDUDatosNormal

  */
 static TPDUDatosNormal crearTPDUDatosNormal (SocketClusterNetImp socketClusterNetImp,
                                  boolean setIR,boolean setACK,boolean setFIN_CONEXION,
                                  boolean setFIN_TRANSMISION,
                                  int numeroRafaga,SecuenceNumber nSec,
                                  Buffer datos)
   throws ClusterNetInvalidParameterException, ClusterNetExcepcion
 {
   final String mn = "TPDUDatosNormal.crearTPDUDatosNormal";

   TPDUDatosNormal resultTPDU = null;

   // Crear el TPDUDatosNormal vacio
   resultTPDU = new TPDUDatosNormal (socketClusterNetImp);
   resultTPDU.BUFFERDATOS = datos;


   // Guardar los datos en la cabecera para cuando sean pedidos
   resultTPDU.IR  = (byte)(setIR  ? 1 : 0);
   resultTPDU.ACK = (byte)(setACK ? 1 : 0);
   resultTPDU.FIN_CONEXION = (byte)(setFIN_CONEXION ? 1 : 0);
   resultTPDU.FIN_TRANSMISION = (byte)(setFIN_TRANSMISION ? 1 : 0);
   resultTPDU.NUMERO_RAFAGA = numeroRafaga;
   resultTPDU.NUMERO_SECUENCIA = (SecuenceNumber)nSec.clone();

   // Crear ID_TPDU_FUENTE
   resultTPDU.ID_TPDU_FUENTE = new ID_TPDU (resultTPDU.getID_SocketEmisor(),
                                             resultTPDU.getNumeroSecuencia());
   return resultTPDU;
 }

 //============================================================================
 /**
  * Crea un TPDUDatosNormal con la informaci�n facilitada.
  * @param puertoMulticast
  * @param puertoUnicast
  * @param clusterGroupID
  * @param dirIP
  * @param setIR
  * @param setACK
  * @param setFIN_CONEXION
  * @param setFIN_TRANSMISION
  * @param numeroRafaga
  * @param nSec numero secuencia
  * @param datos
  * @return objeto TPDUDatosNormal creado
  * @exception ClusterNetInvalidParameterException si alguno de los par�metros es err�neo.
  * @exception ClusterNetExcepcion si hay un error al crear el TPDUACK

  */
 static TPDUDatosNormal crearTPDUDatosNormal (int puertoMulticast,
                                              int puertoUnicast,
                                              ClusterGroupID clusterGroupID,
                                              IPv4 dirIp,
                                              boolean setIR,
                                              boolean setACK,
                                              boolean setFIN_CONEXION,
                                              boolean setFIN_TRANSMISION,
                                              int numeroRafaga,
                                              SecuenceNumber nSec,
                                              Buffer datos)
   throws ClusterNetInvalidParameterException, ClusterNetExcepcion
 {
   final String mn = "TPDUDatosNormal.crearTPDUDatosNormal";

   TPDUDatosNormal resultTPDU = null;

   // Crear el TPDUDatosNormal vacio
   resultTPDU = new TPDUDatosNormal (puertoMulticast,puertoUnicast,clusterGroupID,dirIp);
   // if (datos!=null)
   resultTPDU.BUFFERDATOS = (Buffer)datos;//.clone();
   //else
   //   resultTPDU.BUFFERDATOS = null;

   // Guardar los datos en la cabecera para cuando sean pedidos
   resultTPDU.IR  = (byte)(setIR  ? 1 : 0);
   resultTPDU.ACK = (byte)(setACK ? 1 : 0);
   resultTPDU.FIN_CONEXION = (byte)(setFIN_CONEXION ? 1 : 0);
   resultTPDU.FIN_TRANSMISION = (byte)(setFIN_TRANSMISION ? 1 : 0);
   resultTPDU.NUMERO_RAFAGA = numeroRafaga;
   resultTPDU.NUMERO_SECUENCIA = (SecuenceNumber)nSec.clone();

   // Crear ID_TPDU_FUENTE
   resultTPDU.ID_TPDU_FUENTE = new ID_TPDU (resultTPDU.getID_SocketEmisor(),
                                             resultTPDU.getNumeroSecuencia());

   return resultTPDU;
 }

  //==========================================================================
  /**
   * Construir el TPDU Datos Normal, devuelve un buffer con el contenido del TPDUDatosNormal,
   * seg�n el formato especificado en el protocolo.
   * <b>Este buffer no debe de ser modificado.</B>
   * @return un buffer con el TPDUDatosNormal.
   * @exception ClusterNetExcepcion Se lanza si ocurre alg�n error en la construcci�n
   * del TPDU
   * @exception ClusterNetInvalidParameterException lanzada si ocurre alg�n error en la
   * construcci�n del TPDU
   */
 Buffer construirTPDUDatosNormal () throws ClusterNetExcepcion,ClusterNetInvalidParameterException
 {
   final String mn = "TPDU.construirTPDUDatosNormal";
   int offset = 14;

   // Calcular el tama�o del tpdu a crear.
   int tama�o = TPDUDatosNormal.LONGHEADER;
   if (this.BUFFERDATOS != null)
      tama�o += this.BUFFERDATOS.getMaxLength();

   // Crear la cabecera com�n a todos los TPDU
   Buffer bufferResult = construirCabeceraComun (ClusterNet.SUBTIPO_TPDU_DATOS_NORMAL,
                                                 tama�o);

   // 15� BYTE : ACK : (1 bit)
   short anterior = bufferResult.getByte (offset);
   // anterior : XXXX XXXX
   //      and : 1111 1110 = 0xFE
   //           ----------
   //            XXXX XXX0
   //   ACK    : 0000 000X
   //           ----------
   //            XXXX XXXX
   anterior &= 0xFE;
   bufferResult.addByte((byte)((this.ACK & 0x01) | anterior),offset);
   offset ++;


   // 16�        BYTE : IR (1 bit)

   //              IR : X000  0000

   //     FIN_CONEXION: 0X00  0000

   //  FIN_TRANSMISION: 00X0  0000

   bufferResult.addByte ((byte)(((this.IR<<7)&0x80) | ((this.FIN_CONEXION<<6)&0x40) | ((this.FIN_TRANSMISION<<5)&0x20)),offset);

   offset++;

   // 17� Y 18� BYTE : Tama�o Window
   bufferResult.addShort (this.TAMA�O_VENTANA,offset);
   offset+=2;

   // 19� Y 20� BYTE : N�mero de r�faga
   bufferResult.addShort (this.NUMERO_RAFAGA,offset);
   offset+=2;

   // 21�, 22�, 23� y 24� BYTE : N�mero de Secuencia
   bufferResult.addInt (this.NUMERO_SECUENCIA.tolong(),offset);
   offset+=4;

   // 25� y sucesivos BYTE : Datos
   if (this.BUFFERDATOS!=null)
      bufferResult.addBytes (this.BUFFERDATOS,0,offset,
                      this.BUFFERDATOS.getMaxLength());

   return bufferResult;
 }

  //==========================================================================
  /**
   * Parse un Buffer de datos recibidos y crea un TPDU Datos Normal que lo encapsule.
   * El buffer debe de contener un TPDU Datos Normal.
   * @param buf Un buffer que contiene el TPDU Datos Normal recibido.
   * @param ipv4Emisor direcci�n IP unicast del emisor.
   * @exception ClusterNetExcepcion El buffer pasado no contiene una cabecera TPDU
   * correcta, el mensaje de la excepci�n especifica el tipo de error.
   * @exception ClusterNetInvalidParameterException Se lanza si el buffer pasado no
   * contiene un TPDUDatosNormal v�lido.
   */
 static  TPDUDatosNormal parserBuffer (Buffer buffer,IPv4 ipv4Emisor)
   throws ClusterNetExcepcion,ClusterNetInvalidParameterException
 {
  final String mn = "TPDUDatosNormal.parserBuffer";
  int aux;
  int offset = 14;

  if (buffer==null)
     throw new ClusterNetInvalidParameterException ("Buffer nulo.");

  // El tama�o del buffer debe ser al menos igual a TPDUDatosNormal.LONGHEADER
  if (TPDUDatosNormal.LONGHEADER > buffer.getMaxLength())
    throw new ClusterNetInvalidParameterException ("Buffer no v�lido");

  // Crear el TPDUDatosNormal.
  TPDUDatosNormal tpduDatosNormal = new TPDUDatosNormal ();

  // Analizar los datos comunes CURIOSIDAD: OBSERVAR QUE HAY DOS METODOS
  // EST�TICOS HEREDADOS Y QUE EN PRINCIPIO NO PUEDE DISTINGUIR.
  TPDUDatos.parseCabeceraComun (buffer,tpduDatosNormal,ipv4Emisor);

  // Comprobar que el subtipo es correcto.
  if (tpduDatosNormal.SUBTIPO != ClusterNet.SUBTIPO_TPDU_DATOS_NORMAL)
        throw new ClusterNetExcepcion ("El subtipo del TPDUDatos no es correcto");

  // 15� BYTE : ACK (1 BIT)
  aux = buffer.getByte (offset);


  //     ACK:   XXXX XXXX
  //     And:   0000 0001 = 0x01
  //            ---------
  //            0000 000X
  tpduDatosNormal.ACK = (byte)(aux & 0x01);
  offset++;


  // 16� BYTE : IR (1 bit) FIN_CONEXION(1 bit)
  aux = buffer.getByte (offset);
  //      IR:   XXXX XXXX
  //     And:   1000 0000 = 0x80
  //            ---------
  //            X000 0000
  //     >>>:   0000 000X
  tpduDatosNormal.IR = (byte) ((aux & 0x80) >>> 7);
  //     FIN_CONEXION:   XXXX XXXX
  //     And:   0100 0000 = 0x40
  //            ---------
  //            0X00 0000
  //     >>>:   0000 000X
  tpduDatosNormal.FIN_CONEXION = (byte) ((aux & 0x40) >>> 6);
  //     FIN_TRANSMISION:   XXXX XXXX
  //     And:   0100 0000 = 0x20
  //            ---------
  //            00X0 0000
  //     >>>:   0000 000X
  tpduDatosNormal.FIN_TRANSMISION = (byte) ((aux & 0x20) >>> 5);
  offset++;

  //
  // 17� y 18� BYTE : Tama�o Window
  //
  tpduDatosNormal.TAMA�O_VENTANA = buffer.getShort (offset);
  offset+=2;

  //
  // 19� y 20� BYTE : N�mero R�faga
  //
  tpduDatosNormal.NUMERO_RAFAGA = buffer.getShort (offset);
  offset+=2;

  //
  // 21�, 22�, 23� y 24�  BYTE : N�mero de secuencia
  //
  tpduDatosNormal.NUMERO_SECUENCIA = new SecuenceNumber (buffer.getInt (offset));
  offset+=4;

  // Crear ID_TPDU_FUENTE
  tpduDatosNormal.ID_TPDU_FUENTE = new ID_TPDU (tpduDatosNormal.getID_SocketEmisor(),
                                                     tpduDatosNormal.getNumeroSecuencia());

  //
  // 25� y sucesivos BYTES : Datos
  //
  int tama�oDatos = buffer.getMaxLength() - offset;
  if (tama�oDatos>0)
     tpduDatosNormal.BUFFERDATOS = new Buffer (buffer.getBytes (offset,tama�oDatos));
  else tpduDatosNormal.BUFFERDATOS = null;

  return tpduDatosNormal;
 }


 //============================================================================
 /**
  * Devuelve el n�mero de secuencia.
  */
 SecuenceNumber getNumeroSecuencia ()
 {
   return this.NUMERO_SECUENCIA;
 }

 //============================================================================
 /**
  * Devuelve el tama�o de ventana.
  */
 int getTama�oVentana ()
 {
  return ClusterNet.TAMA�O_VENTANA_RECEPCION;
 }

 //===========================================================================
 /**
  * Devuelve el ID_TPDU.
  */
 ID_TPDU getID_TPDU ()
 {
  return this.ID_TPDU_FUENTE;
 }

 //===========================================================================
 /**
  * Devuelve el n�mero de la r�faga.
  */
 int getNumeroRafaga ()
 {
    return this.NUMERO_RAFAGA;
 }


 //===========================================================================

 /**
  * Devuelve true si el bit IR vale 1.
  */
 boolean getIR ()
 {

  if (this.IR == 1)
   return true;

  return false;
 }



 //===========================================================================
 /**
  * Devuelve true si el bit FIN_CONEXION vale 1.
  */
 boolean getFIN_CONEXION()
 {
  if (this.FIN_CONEXION == 1)
   return true;

  return false;
 }


 //===========================================================================

 /**
  * Devuelve true si el bit FIN_TRANSMISION vale 1.
  */
 boolean getFIN_TRANSMISION()
 {

  if (this.FIN_TRANSMISION == 1)
   return true;

  return false;
 }


 //===========================================================================
 /**
  * Devuelve true si el bit ACK vale 1.
  */
 boolean getACK ()
 {

  if (this.ACK == 1)
   return true;

  return false;
 }


 //===========================================================================
 /**
  * Devuelve el buffer de datos.
  */
 Buffer getBufferDatos ()
 {

  return this.BUFFERDATOS;

 }


 //===========================================================================
 /**
  * Devuelve una cadena informativa.
  */
 public String toString()
 {

   return "===================================================="+
          "\nPuerto Multicast: " + this.getPuertoMulticast() +
          "\nPuerto Unicast: " + this.getPuertoUnicast() +
          "\nIDGL: " + this.ID_GRUPO_LOCAL +
          "\nLongitud: " + this.LONGITUD +
          "\nCHECKSUM: " + this.CHEKSUM +
          "\nVersion: " + this.VERSION +
          "\nTipo: " + this.TIPO +
          "\nSubtipo: " + ClusterNet.SUBTIPO_TPDU_DATOS_NORMAL +
          "\nACK: " + this.ACK +
          "\nIR: " + this.IR +
          "\nFIN_CONEXION: " + this.FIN_CONEXION +
          "\nFIN_TRANSMISION: " + this.FIN_TRANSMISION +
          "\nN�mero R�faga: " + this.NUMERO_RAFAGA +
          "\nN�mero Secuencia: " + this.NUMERO_SECUENCIA+
          "\n====================================================";
          //"\nDatos: " + this.BUFFERDATOS+

 }


} // Fin de la clase.


