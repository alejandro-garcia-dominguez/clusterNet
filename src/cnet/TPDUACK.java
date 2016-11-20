//============================================================================
//
//	Copyright (c) 1999-2015 . All Rights Reserved.
//
//----------------------------------------------------------------------------
//
//	Fichero: TPDUACK.java  1.0 9/9/99
//
//
//	Descripci�n: Clase TPDUACK.
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

import java.util.*;
import java.lang.*;


/**
 * Clase TPDU ACK.<br>
 * Hereda de la clase TPDUDatos.<br>
 *
 * Para crear un objeto de esta clase se tienen que usar los m�todos est�ticos.
 * Una vez creado no puede ser modicado.<br>
 *
 * El formato completo del TPDU ACK es: <br>
 *
 *                      1 1 1 1 1 1 1 1 1 1 2 2 2 2 2 2 2 2 2 2 3 3<br>
 * +0-1-2-3-4-5-6-7-8-9-0-1-2-3-4-5-6-7-8-9-0-1-2-3-4-5-6-7-8-9-0-1<br>
 * +---------------------------------------------------------------+<br>
 * +      Puerto Mulitcast         |          Puerto Unicast       +<br>
 * +---------------------------------------------------------------+<br>
 * +                    ID_GRUPO_LOCAL(4 bytes primeros)           +<br>
 * +---------------------------------------------------------------+<br>
 * +ID_GRUPO_LOCAL(2 bytes �ltimos)|          Longitud             +<br>
 * +---------------------------------------------------------------+<br>
 * +           Cheksum             | V |0|1|0|1|1|    No Usado     +<br>
 * +---------------------------------------------------------------+<br>
 * +    N�mero de R�faga Fuente    |      Direcci�n IP Fuente      +<br>
 * +                               |      (16 bits superiores)     +<br>
 * +---------------------------------------------------------------+<br>
 * +    Direcci�n IP Fuente        |      Puerto Unicast Fuente    +<br>
 * +    (16 bits superiores)       |                               +<br>
 * +---------------------------------------------------------------+<br>
 * +                   N�mero de Secuencia Fuente                  +<br>
 * +---------------------------------------------------------------+<br>
 * <br>
 * <br>
 * Esta clase no es thread-safe.<br>
 * @see      Buffer
 * @version  1.0
 * @author M. Alejandro Garc�a Dom�nguez
 * <A HREF="mailto:alejandro.garcia.dominguez@gmail.com">(alejandro.garcia.dominguez@gmail.com)</A><p>
 * Antonio Berrocal Piris
 * <A HREF="mailto:AntonioBP.wanadoo.es">(AntonioBP@wanadoo.es)</A><p>
 */

public class TPDUACK extends TPDUDatos
{
  // ATRIBUTOS
  /** Tama�o de la cabecera del TPDUACK*/
  static final int LONGHEADER = 7 * 4;

 /**
  * N�mero de R�faga Fuente (16 bits):
  */
  int NUMERO_RAFAGA_FUENTE = 0;

 /**
  * Direcci�n IP Fuente (32 bits): direcci�n IP del socket que origin� los datos.
  */
  IPv4 DIR_IP_FUENTE = null;

 /**
  * Puerto Unicast Fuente (16 bits): puerto unicast fuente
  */
  int PUERTO_UNICAST_FUENTE = 0;

 /**
  * N�mero de secuencia (32 bits): n�mero de secuencia del TPDU que estoy
  * asentiendo.
  */
  SecuenceNumber NUMERO_SECUENCIA_FUENTE = null;


  /**
   * Se forma con el valor de otros campos.<br>
   * <ul>ID TPDU Fuente : (10 bytes)
   *                    <li><ul>ID Socket Fuente (6 byte)
   *                            <li>Direcci�n IP Fuente (4 byte)</li>
   *                            <li>Puerto Unicast Fuente (2 byte)</li></ul></li>
   *                    <li>N�mero secuencia Fuente (4 bytes)</li></ul>
   */
   private ID_TPDU ID_TPDU_FUENTE = null;



  //==========================================================================
  /**
   * Constructor por defecto.
   * Este constructor es para crear TPDUS a partir del parser de un Buffer
   * @exception ClusterNetExcepcion
   * @exception ParametroInvalido Se lanza en el constructor de la clase TPDU.
   */
  private TPDUACK ()

      throws ClusterNetInvalidParameterException,ClusterNetExcepcion

  {

   super();

  }


  //==========================================================================
  /**
   * Constructor utilizado para crear un TPDUACK.
   * @param socketClusterNetImp Objeto SocketClusterNetImp del que obtiene el valor de los
   * campos de la cabecera com�n.
   * @exception ClusterNetExcepcion
   * @exception ClusterNetInvalidParameterException lanzada si socketClusterNetImp es null
   */
  private TPDUACK (SocketClusterNetImp socketClusterNetImp)
    throws ClusterNetExcepcion,ClusterNetInvalidParameterException
  {
   super (socketClusterNetImp);
  }


 //============================================================================
 /**
  * Crea un TPDUACK con la informaci�n facilitada.
  * @param socketClusterNetImp Objeto SocketClusterNetImp del que obtiene el valor de los
  * campos de la cabecera com�n.
  * @param numeroRafagaFuente n�mero de r�faga fuente
  * @param dirIPFuente direcci�n IP Fuente
  * @param nSec n�mero secuencia
  * @param puertoUnicastFuente puerto unicast fuente
  * @return objeto TPDUACK creado.
  * @exception ClusterNetInvalidParameterException si alguno de los par�metros es err�neo.
  * @exception ClusterNetExcepcion si hay un error al crear el TPDUACK

  */
  static TPDUACK crearTPDUACK (SocketClusterNetImp socketClusterNetImp,
                              int numeroRafagaFuente,
                              IPv4 dirIPFuente,
                              SecuenceNumber nSec,
                              int puertoUnicastFuente)
   throws ClusterNetInvalidParameterException, ClusterNetExcepcion
  {

   // Crear el TPDUDatos vacio
   TPDUACK resultTPDU = new TPDUACK (socketClusterNetImp);

   // Guardar los datos en la cabecera para cuando sean pedidos
   resultTPDU.NUMERO_RAFAGA_FUENTE    = numeroRafagaFuente;
   resultTPDU.DIR_IP_FUENTE           = dirIPFuente;
   resultTPDU.NUMERO_SECUENCIA_FUENTE = (SecuenceNumber)nSec.clone();
   resultTPDU.PUERTO_UNICAST_FUENTE   = puertoUnicastFuente;

   return resultTPDU;
 }


  //==========================================================================
  /**
   * Construir el TPDU ACK, devuelve un buffer con el contenido del TPDUACK,
   * seg�n el formato especificado en el protocolo.
   * <b>Este buffer no debe de ser modificado.</B>
   * @return un buffer con el TPDUACK.
   * @exception ClusterNetExcepcion Se lanza si ocurre alg�n error en la construcci�n
   * del TPDU
   * @exception ClusterNetInvalidParameterException lanzada si ocurre alg�n error en la
   * construcci�n del TPDU
   */
 Buffer construirTPDUACK () throws ClusterNetExcepcion,ClusterNetInvalidParameterException
 {
   final String mn = "TPDU.construirTPDUDatos";
   int offset = 14;


   // Crear la cabecera com�n a todos los TPDU
   Buffer bufferResult = construirCabeceraComun (ClusterNet.SUBTIPO_TPDU_DATOS_ACK,
                                                 TPDUACK.LONGHEADER);

   if (bufferResult == null)
    throw new ClusterNetExcepcion ("No se ha podido crear el buffer");

   // 15� BYTE : Subtipo: (3 bits )
   short anterior = bufferResult.getByte (offset);
   // anterior : XXXX XXXX
   //      and : 1111 1110 = 0xFE
   //           ----------
   //            XXXX XXX0
   anterior &= 0xFE;
   bufferResult.addByte((byte)anterior,offset);
   offset ++;


   // 16� BYTE : No usado

   bufferResult.addByte ((byte)0,offset);

   offset ++;


   // 17� y 18� BYTE : N�mero de R�faga Fuente

   bufferResult.addShort (this.NUMERO_RAFAGA_FUENTE,offset);

   offset+=2;


   // 19�, 20�, 21� y 22� BYTE : Direcci�n IP Fuente
   bufferResult.addBytes (this.DIR_IP_FUENTE.ipv4,0,offset,4);
   offset+=4;

   // 23� y 24� BYTE : Puerto Unicast Fuente
   bufferResult.addShort (this.PUERTO_UNICAST_FUENTE,offset);
   offset+=2;

   // 25�, 26�, 27� y 28� BYTE : N�mero de Secuencia
   bufferResult.addInt (this.NUMERO_SECUENCIA_FUENTE.tolong(),offset);
   offset += 4;


   return bufferResult;
}

  //==========================================================================
  /**
   * Parse un Buffer de datos recibidos y crea un TPDU ACK que lo encapsule.
   * El buffer debe de contener un TPDU ACK.
   * @param buf Un buffer que contiene el TPDU ACK recibido.
   * @param ipv4Emisor direcci�n IP unicast del emisor del ACK.
   * @return Un objeto TPDUACK
   * @exception ClusterNetExcepcion El buffer pasado no contiene una cabecera TPDU
   * correcta, el mensaje de la excepci�n especifica el tipo de error.
   * @exception ClusterNetInvalidParameterException Se lanza si el buffer pasado no
   * contiene un TPDUACK v�lido.
   */
 static  TPDUACK parserBuffer (Buffer buffer,IPv4 ipv4Emisor)
   throws ClusterNetExcepcion,ClusterNetInvalidParameterException
 {
  int aux;
  int offset = 16;

  // Crear el TPDUDatos.
  if (buffer==null)
     throw new ClusterNetInvalidParameterException ("Buffer nulo");

  TPDUACK tpduACK = new TPDUACK ();

  // Analizar los datos comunes
  TPDUDatos.parseCabeceraComun (buffer,tpduACK,ipv4Emisor);

  // Comprobar si el tipo es correcto
  if (tpduACK.SUBTIPO != ClusterNet.SUBTIPO_TPDU_DATOS_ACK)
      throw new ClusterNetExcepcion ("El subtipo del TPDU Datos no es correcto");

  // 15� BYTE : SUBTIPO (3 BITS) ACK (1 BIT)

  //
  // 16� BYTE : No usado
  //

  //
  // 17� y 18� BYTE : N�mero de r�faga fuente
  //
  tpduACK.NUMERO_RAFAGA_FUENTE = buffer.getShort (offset);
  offset+=2;

  //
  // 19�, 20�, 21� y 22� BYTE : Direcci�n IP fuente
  //
  tpduACK.DIR_IP_FUENTE = new IPv4 (new Buffer (buffer.getBytes (offset,4)));
  offset+=4;

  //
  // 23� y 24� BYTE : Puerto Unicast Fuente
  //
  tpduACK.PUERTO_UNICAST_FUENTE = buffer.getShort (offset);
  offset += 2;

  //
  // 25�, 26�, 27� y 28� BYTE : N�mero de secuencia
  //
  tpduACK.NUMERO_SECUENCIA_FUENTE = new SecuenceNumber (buffer.getInt (offset));
  offset+=4;

  return tpduACK;
 }




 //===========================================================================

 /**

  * Devuelve una cadena informativa del TPDU ACK

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
          "\nN�mero R�faga Fuente: " + this.NUMERO_RAFAGA_FUENTE +
          "\nIP fuente: " + this.DIR_IP_FUENTE +
          "\nPuerto Unicast Fuente: " + this.PUERTO_UNICAST_FUENTE +
          "\nN�mero Secuencia Fuente: " + this.NUMERO_SECUENCIA_FUENTE +
          "\nSubtipo: " + ClusterNet.SUBTIPO_TPDU_DATOS_ACK+
          "\n====================================================";



 }



//==========================================================================

/**

 * Devuelve el {@link #ID_TPDU_FUENTE ID_TDPU Fuente}.

 */

ID_TPDU getID_TPDUFuente ()

 {

  if (ID_TPDU_FUENTE == null)

   {

    try {

       ClusterMemberID id_SocketFuente = new ClusterMemberID

                                (this.DIR_IP_FUENTE,this.PUERTO_UNICAST_FUENTE);

       this.ID_TPDU_FUENTE = new ID_TPDU (id_SocketFuente,this.NUMERO_SECUENCIA_FUENTE);

     } catch (ClusterNetInvalidParameterException e){}

    }

  return this.ID_TPDU_FUENTE;

 }


//==========================================================================

/**

 * Devuelve el n�mero de r�faga fuente.

 */

 int getNumeroRafagaFuente ()

 {

  return this.NUMERO_RAFAGA_FUENTE;

 }



} // Fin de la clase.





























