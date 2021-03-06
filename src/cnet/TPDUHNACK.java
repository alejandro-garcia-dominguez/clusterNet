//============================================================================
//
//	Copyright (c) 1999-2015. All Rights Reserved.
//
//----------------------------------------------------------------------------
//
//	File: TPDUDatosHNACK.java  1.0 20/09/99
//
//	Descripci�n: Clase TPDUDatosHNACK
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

import java.util.Iterator;
import java.util.Vector;

/**
 * Clase TPDU HNACK.<br>
 * Hereda de la clase TPDUDatos.<br>
 *
 * Para crear un objeto de esta clase se tienen que usar los m�todos est�ticos.
 * Una vez creado no puede ser modicado.<br>
 *
 * El formato completo del TPDU HNACK es: <br>
 * <br>
 *                      1 1 1 1 1 1 1 1 1 1 2 2 2 2 2 2 2 2 2 2 3 3<br>
 * +0-1-2-3-4-5-6-7-8-9-0-1-2-3-4-5-6-7-8-9-0-1-2-3-4-5-6-7-8-9-0-1<br>
 * +---------------------------------------------------------------+<br>
 * +      Puerto Mulitcast         |          Puerto Unicast       +<br>
 * +---------------------------------------------------------------+<br>
 * +                        ID_GRUPO_LOCAL                         +<br>
 * +                      (4 bytes primeros)                       +<br>
 * +---------------------------------------------------------------+<br>
 * +        ID_GRUPO_LOCAL         |          Longitud             +<br>
 * +      (2 bytes �ltimos)        |                               +<br>
 * +---------------------------------------------------------------+<br>
 * +           Cheksum             | V |0|1|1|1|1|    No Usado     +<br>
 * +---------------------------------------------------------------+<br>
 * +                          Direcci�n IP 1                       +<br>
 * +---------------------------------------------------------------+<br>
 * +       Puerto Unicast 1        |     Numero de Secuencia 1     +<br>
 * +                               |     (16 bits superiores)      +<br>
 * +---------------------------------------------------------------+<br>
 * +      N�mero de Secuencia 1    |         Direcci�n IP 2        +<br>
 * +      (16 bits inferiores)     |       (16 bits superiores)    +<br>
 * +---------------------------------------------------------------+<br>
 * +         Direcci�n IP 2        |         Puerto Unicast 2      +<br>
 * +      (16 bits inferiores)     |                               +<br>
 * +---------------------------------------------------------------+<br>
 * +                      N�mero de Secuencia 2                    +<br>
 * +---------------------------------------------------------------+<br>
 * +                              ...                              +<br>
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
public class TPDUHNACK extends TPDUDatos
{

  // ATRIBUTOS
  /** Tama�o de la cabecera del TPDUHNACK*/
  static final int LONGHEADER = 4*4 ;

   /**
    * Lista con los id_tpuds que solicita por no haberlos recibido.
    * <table border=1>
    *  <tr>  <td><b>Key:</b></td>
    *	    <td>{@link ID_TPDU} no recibido.</td>
    *  </tr>
    *  <tr>  <td><b>Value:</b></td>
    *	    <td>null</td>
    *  </tr>
    * </table>
    */
   ListaOrdID_TPDU LISTA_ORD_ID_TPDU = null;

  //==========================================================================
  /**
   * Constructor utilizado para crear un TPDUHNACK.
   * @param socketClusterNetImp Objeto SocketClusterNetImp del que obtiene el valor de los
   * campos de la cabecera com�n.
   * @exception ClusterNetExcepcion
   * @exception ClusterNetInvalidParameterException lanzada si socketClusterNetImp es null
   */
  private TPDUHNACK (SocketClusterNetImp socketClusterNetImp)
    throws ClusterNetExcepcion,ClusterNetInvalidParameterException
  {
   super (socketClusterNetImp);
  }

  //==========================================================================
  /**
   * Constructor por defecto.
   * Este constructor es para crear TPDUS a partir del parser de un Buffer
   * @exception ClusterNetExcepcion
   * @exception ParametroInvalido Se lanza en el constructor de la clase TPDU.
   */
  private TPDUHNACK ()
      throws ClusterNetInvalidParameterException,ClusterNetExcepcion
  {
   super();
  }

 //============================================================================
 /**
  * Crea un TPDUHNACK con la informaci�n facilitada.
  * @param socketClusterNetImp Objeto SocketClusterNetImp del que obtiene el valor de los
  * campos de la cabecera com�n.
  * @param listaID_TPDU lista con los ID_TPDU solicitados
  * @return objeto TPDUHNACK creado.
  * @exception ClusterNetInvalidParameterException si alguno de los par�metros es err�neo.
  * @exception ClusterNetExcepcion si hay un error al crear el TPDUHNACK
  */
 static TPDUHNACK crearTPDUHNACK (SocketClusterNetImp socketClusterNetImp,
                                ListaOrdID_TPDU listaID_TPDU)
   throws ClusterNetInvalidParameterException, ClusterNetExcepcion
 {
   TPDUHNACK resultTPDU = null;

   if (listaID_TPDU == null)
      throw new ClusterNetExcepcion ("La lista pasada no puede ser null.");

   // Crear el TPDUHNACK vacio. Cada ID_TPDU ocupa 10 bytes
   resultTPDU = new TPDUHNACK (socketClusterNetImp);

   // Guardar los datos en la cabecera para cuando sean pedidos
   resultTPDU.LISTA_ORD_ID_TPDU = (ListaOrdID_TPDU)listaID_TPDU.clone();

   return resultTPDU;
 }

  //==========================================================================
  /**
   * Construir el TPDU HNACK, devuelve un buffer con el contenido del TPDUHNACK,
   * seg�n el formato especificado en el protocolo.
   * <b>Este buffer no debe de ser modificado.</B>
   * @return un buffer con el TPDUHNACK.
   * @exception ClusterNetExcepcion Se lanza si ocurre alg�n error en la construcci�n
   * del TPDU
   * @exception ClusterNetInvalidParameterException lanzada si ocurre alg�n error en la
   * construcci�n del TPDU
   */
 Buffer construirTPDUHNACK () throws ClusterNetExcepcion,ClusterNetInvalidParameterException
 {
  final String mn = "TPDU.construirTPDUHNACK";
  int offset = 14;


   int tama�o = TPDUHNACK.LONGHEADER + this.LISTA_ORD_ID_TPDU.size()*10;

   // Crear la cabecera com�n a todos los TPDU
   Buffer bufferResult =this.construirCabeceraComun (ClusterNet.SUBTIPO_TPDU_DATOS_HNACK,tama�o);

   if (bufferResult==null)
        throw new ClusterNetExcepcion (mn + "Error en el parser");


   // 15� BYTE : Subtipo: 110 ACK : (1 bit)
   short anterior = bufferResult.getByte (offset);
   // anterior : XXXX XXXX
   //      and : 1111 1110 = 0xFE
   //           ----------
   //            XXXX XXX0
   anterior &= 0xFE;
   bufferResult.addByte((byte)anterior,offset);
   offset++;

   // 16� BYTE : IR (1 bit) N�mero de IP (7 bits).
   //   IR : 0000  0000
   bufferResult.addByte ((byte)0,offset);
   offset++;

   // 17� y sucesivos BYTE : [IP,Puerto Unicast,N�mero Secuencia]
   Iterator iteradorID_TPDU = this.LISTA_ORD_ID_TPDU.iteradorID_TPDU();
   ID_TPDU id_TPDUNext  = null;
   ClusterMemberID id_Socket  = null;
   SecuenceNumber nSec = null;
   while (iteradorID_TPDU.hasNext())
    {
     id_TPDUNext = (ID_TPDU)iteradorID_TPDU.next();
     id_Socket   = id_TPDUNext.getID_Socket ();
     nSec        = id_TPDUNext.getNumeroSecuencia ();

     // A�adir IP
     bufferResult.addBytes (id_Socket.getDireccion().ipv4,0,offset,4);
     offset += 4;
     // A�adir Puerto Unicast
     bufferResult.addShort (id_Socket.getPuertoUnicast(),offset);
     offset += 2;
     // A�adir el n�mero de secuencia
     bufferResult.addInt (nSec.tolong(),offset);
     offset += 4;
    } // Fin del while

   return bufferResult;

 }

  //==========================================================================
  /**
   * Parse un Buffer de datos recibidos y crea un TPDU HNACK que lo encapsule.
   * El buffer debe de contener un TPDU HNACK.
   * @param buffer Un buffer que contiene el TPDU HNACK recibido.
   * @param ipv4Emisor direcci�n IP unicast del emisor.
   * @return Un objeto TPDUHNACK
   * @exception ClusterNetExcepcion El buffer pasado no contiene una cabecera TPDU
   * correcta, el mensaje de la excepci�n especifica el tipo de error.
   * @exception ClusterNetInvalidParameterException Se lanza si el buffer pasado no
   * contiene un TPDUHNACK v�lido.
   */
 static  TPDUHNACK parserBuffer (Buffer buffer,IPv4 ipv4Emisor)
   throws ClusterNetExcepcion,ClusterNetInvalidParameterException
 {
  final String mn = "TPDUHNACK.parserBuffer";
  int aux;
  int offset = 16;


  if (buffer==null)
     throw new ClusterNetInvalidParameterException (mn + "Buffer nulo");

  if (TPDUHNACK.LONGHEADER > buffer.getMaxLength())
     throw new ClusterNetInvalidParameterException (mn + "Buffer incorrecto");

  // Crear el TPDUHNACK.
  TPDUHNACK tpduHNACK = new TPDUHNACK ();

  // Analizar los datos comunes
  TPDUDatos.parseCabeceraComun (buffer,tpduHNACK,ipv4Emisor);

  // Comprobar si el tipo es correcto
  if (tpduHNACK.SUBTIPO != ClusterNet.SUBTIPO_TPDU_DATOS_HNACK)
      throw new ClusterNetExcepcion (mn+"Subtipo del TPDU Datos no es HNACK");

  // 13� BYTE : SUBTIPO (3 BITS) ACK (1 BIT)

  // 14� BYTE : No usado

  // 15� y sucesivos BYTE : [IP,Puerto Unicast,N�mero Secuencia]
  // Crear la lista

  // Crear la lista
  tpduHNACK.LISTA_ORD_ID_TPDU = new ListaOrdID_TPDU ();

  ID_TPDU id_TPDU      = null;
  ClusterMemberID id_Socket  = null;
  IPv4           ipv4  = null;
  while (offset<buffer.getMaxLength())
   {
    // Obtener Direcci�n IP
    ipv4 = new IPv4 (new Buffer (buffer.getBytes(offset,4)));
    offset += 4;
    // Unir la Direcci�n IP con el puerto unicast que se obtenga
    id_Socket = new ClusterMemberID (ipv4,buffer.getShort (offset));
    offset += 2;

    // Unir id_Socket con el n�mero de secuencia que se obtenga
    id_TPDU = new ID_TPDU (id_Socket,new SecuenceNumber (buffer.getInt (offset)));
    offset += 4;

    tpduHNACK.LISTA_ORD_ID_TPDU.put (id_TPDU,null);
    } // Fin del while

  return tpduHNACK;
 }

 //===========================================================================
 /**
  * Devuelve una cadena informativa del TPDUHNACK
  */
 public String toString()
 {
   String result = new String (
          "Puerto Multicast: " + this.getPuertoMulticast () +
          "\nPuerto Unicast: " + this.getPuertoUnicast () +
          "\nIDGL: " + this.ID_GRUPO_LOCAL +
          "\nLongitud: " + this.LONGITUD +
          "\nCHECKSUM: " + this.CHEKSUM +
          "\nVersion: " + this.VERSION +
          "\nTipo: " + this.TIPO +
          "\nSubtipo: " + ClusterNet.SUBTIPO_TPDU_DATOS_HNACK
         );
   // A�adir al String los id. socket
   if (this.LISTA_ORD_ID_TPDU != null)
     result = result + "\nID_TPDU: " + this.LISTA_ORD_ID_TPDU;

   return result;
 }

  //===========================================================================
  /**
   * Devuelve <b>una copia</b> de la lista de id_tpdus solicitados.
   * @see #LISTA_ORD_ID_TPDU
   */
  public ListaOrdID_TPDU getListaID_TPDU ()
  {
   return (ListaOrdID_TPDU)this.LISTA_ORD_ID_TPDU.clone();
  }


}

