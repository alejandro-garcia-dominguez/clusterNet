//============================================================
//
//	Copyright (c) 1999-2015 . All Rights Reserved.
//
//------------------------------------------------------------
//
//	File: SecuenceNumber.java  1.0
//
//	Descripci�n: Clase SecuenceNumber. Operaciones y almacen para
//                   manejar un n�mero de secuencia.
//
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

import java.lang.Cloneable;
import java.lang.Comparable;


/**
 * Clase que almacena un n�mero de secuencia y ofrece las operaciones
 * necesarias para su manejo. <br>
 * Un n�mero de secuencia est� formado por un entero de 32 bits (sin signo)
 * que identifica un {@link TPDUDatosNormal} enviado por un id_socket.<br>
 *
 * <b>Una vez creado no puede ser modificado.</b>
 * @version 1.0
 * @author Antonio Berrocal Piris
 * <A HREF="mailto:AntonioBP@wanadoo.es">(AntonioBP@wanadoo.es)</A><p>
 * M. Alejandro Garc�a Dom�nguez
 * <A HREF="mailto:alejandro.garcia.dominguez@gmail.com">(alejandro.garcia.dominguez@gmail.com)</A><p>
 */
public class SecuenceNumber implements Cloneable,Comparable

{

 /** Almacena el valor del n�mero de secuencia. */
  private long lNumeroSecuencia = 1;

  //=================================================================
  /**
   * Crea un objeto n�mero de secuencia con el valor indicado.
   * @param numSec valor del n�mero de secuencia
   * @exception ClusterNetInvalidParameterException lanzada si el n�mero de secuencia no es
   * v�lido.
   */
   public SecuenceNumber (long lNumSec) throws ClusterNetInvalidParameterException
   {
    final String mn = "SecuenceNumber.Numerosecuencia(long)";
    if (esValido (lNumSec))
        this.lNumeroSecuencia = lNumSec;
    else throw new ClusterNetInvalidParameterException ("El n�mero de secuencia " + lNumSec +
                                                " no es v�lido.");
   }

  //=================================================================
  /**
   * Comprueba que el n�mero de secuencia indicado es v�lido, para lo cual
   * debe ser mayor o igual a cero.
   * @param numSec n�mero de secuencia
   * @return true si el n�mero de secuencia es v�lido, false en caso contrario.
   */
  private boolean esValido (long lNumSec)
  {
   if (lNumSec < 0)
        return false;
   return true;
  }

  //=================================================================
  /**
   * Devuelve el n�mero de secuencia como un long.
   * @return n�mero de secuencia como long
   */
  public long tolong()
  {
     return this.lNumeroSecuencia;
  }

   //=================================================================
   /**
    * Implementa el m�todo de la interfaz {@link Comparable}.
    * @param o n�mero de secuencia para comparar con este, si no es una
    * instancia de {@link SecuenceNumber} se lanzar� la excepci�n
    * {@link java.lang.ClassCastException}.
    * @return -1 si este n�mero de secuencia es menor que el dado,
    * 1 si es mayor y 0 si son iguales.
    */
   public int compareTo(Object o)
   {
    SecuenceNumber ns = (SecuenceNumber) o;

    if (this.lNumeroSecuencia<ns.tolong())
        return -1;
    if (this.lNumeroSecuencia>ns.tolong())
        return 1;
    return 0;
   }

   //=================================================================
   /**
    * Comprueba si este n�mero de secuencia es igual al pasado por par�metro.
    * @return true si son iguales, y false en caso contrario.
    */
   public boolean equals (Object o)
   {
    SecuenceNumber ns = (SecuenceNumber) o;

    if (this.lNumeroSecuencia==ns.tolong())
        return true;
    return false;
   }

   //=================================================================
   /**
    * Comprueba si este n�mero de secuencia es igual al pasado por par�metro.
    * @return true si son iguales, y false en caso contrario.
    */
   public boolean igual (SecuenceNumber o)
   {
    if (this.compareTo(o)==0)
        return true;
    return false;
   }

   //=================================================================
   /**
    * Comprueba si este n�mero de secuencia es mayor o igual al pasado por par�metro.
    * @return true si son iguales, y false en caso contrario.
    */
   public boolean mayorIgual (SecuenceNumber o)
   {
    if (this.compareTo(o)>=0)
        return true;
    return false;
   }

   //=================================================================
   /**
    * Comprueba si este n�mero de secuencia es mayor al pasado por par�metro.
    * @return true si es mayor, y false en caso contrario.
    */
   public boolean mayor (SecuenceNumber o)
   {
    if (this.compareTo (o)>0)
        return true;
    return false;
   }

   //=================================================================
   /**
    * Comprueba si este n�mero de secuencia es menor al pasado por par�metro.
    * @return true si es menor, y false en caso contrario.
    */
   public boolean menor (SecuenceNumber o)
   {
    if (this.compareTo (o)<0)
        return true;
    return false;
   }

   //=================================================================
   /**
    * Comprueba si este n�mero de secuencia es menor o igual al pasado por par�metro.
    * @return true si es menor, y false en caso contrario.
    */
   public boolean menorIgual (SecuenceNumber o)
   {
    if (this.compareTo (o)<=0)
        return true;
    return false;
   }

   //=================================================================
   /**
    * Devuelve el n�mero de secuencia siguiente.
    * @return n�mero de secuencia siguiente
    */
   public long getSiguiente ()
   {
     return (this.lNumeroSecuencia+1);
   }

   //=================================================================
   /**
    * Devuelve el n�mero de secuencia anterior, o 0 si no existe.
    * @return n�mero de secuencia anterior, o 0 si no existe.
    */
   public SecuenceNumber getAnterior () throws ClusterNetExcepcion
   {
    try {
      if (this.lNumeroSecuencia>0)
            return new SecuenceNumber (this.lNumeroSecuencia-1);
     }catch (ClusterNetInvalidParameterException e)
         {throw new ClusterNetExcepcion (e.toString());}

     throw new ClusterNetExcepcion ("El n�mero de secuencia no es v�lido.");
   }

   //=================================================================
   /**
    * Incrementa el n�mero de secuencia en la cantidad indicada.
    * Si cantidad es menor que cero, no se incrementa.
    * @param iCantidad en que se incrementa el n�mero de secuencia
    * @exception ClusterNetInvalidParameterException
    */
   public SecuenceNumber incrementar (long lCantidad)
      throws ClusterNetInvalidParameterException
   {
    return new SecuenceNumber (this.lNumeroSecuencia+lCantidad);
   }

   //=================================================================
   /**
    * Decrementa el n�mero de secuencia en una unidad si es mayor que cero.
    * @throws  ClusterNetExcepcion
    */
   public SecuenceNumber decrementar ()
      throws ClusterNetExcepcion
   {
    // Al crearse el nuevo n�mero se lanza la excepci�n si no es v�lido.
    try{
      return new SecuenceNumber (this.lNumeroSecuencia-1);
     }catch (ClusterNetInvalidParameterException e)
        {throw new ClusterNetExcepcion (e.toString());}
   }

   //=================================================================
   /**
    * Crea un copia de este n�mero de secuencia. Lo que devulve no es una referencia
    * a este objeto, sino un nuevo objeto cuyos datos son copias de este.
    * @return n�mero de secuencia clon de este.
    */
   public Object clone()
   {
    final String mn = "SecuenceNumber.clone";
     try{
       return new SecuenceNumber (this.lNumeroSecuencia);
      }catch (ClusterNetInvalidParameterException e)
             {}
     return null;

   }

   //=================================================================
   /**
    * Devuelve el n�mero de secuencia mayor posible (LIMITESUPERIOR).
    */
   public static SecuenceNumber LIMITESUPERIOR;

   static {
    try
    {
     LIMITESUPERIOR = new SecuenceNumber (Long.MAX_VALUE-1);
    }
    catch(java.lang.Exception e)
    {
      ;
    }
   }

   //=================================================================
   /**
    * Devuelve el n�mero de secuencia menor posible (LIMITEINFERIOR).
    */
   public static SecuenceNumber LIMITEINFERIOR;

   static {
     try
     {
      LIMITEINFERIOR = new SecuenceNumber (1);
     }
     catch(java.lang.Exception e)
     {
      ;
     }
   }

   //=================================================================
   /**
    * Devuelve una cadena representaci�n del n�mero de secuencia.
    * @return cadena representaci�n del n�mero de secuencia.
    */
   public String toString ()
   {
    return "NSec: " + this.lNumeroSecuencia;
   }




}
