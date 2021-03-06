//============================================================
//
//	Copyright (c) 1999-2015 . All Rights Reserved.
//
//------------------------------------------------------------
//
//	Fichero: ClusterTimer.java  1.0
//
//
//	Descripci�n: Clase ClusterTimer. Proporciona m�todos �tiles para
//                   manejar temporizaciones. Avisos premeditados,....
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

import java.util.LinkedList;

/**
 * Clase que proporciona m�todos para temporizar eventos a trav�s de
 * callbacks.<br>
 * <br>
 * <p>Utiliza un thread para medir el tiempo.
 * <p>Se puede acceder concurrentemente de forma segura (es thread-safe).
 * <p>Es independiente de cualquier otra clase, s�lo utiliza la interfaz
 * {@link TimerHandler} para realizar callbacks.
 * @version  1.0
 * @author Antonio Berrocal Piris
 * <A HREF="mailto:AntonioBP@wanadoo.es">(AntonioBP@wanadoo.es)</A><p>
 * M. Alejandro Garc�a Dom�nguez
 * <A HREF="mailto:alejandro.garcia.dominguez@gmail.com">(alejandro.garcia.dominguez@gmail.com)</A><p>
 */
public class  ClusterTimer
{

 // ATRIBUTOS

 /**
  * Cola con la lista de vencimientos de los id_tpdu registrados para avisar
  * cuando se supere el tiempo m�ximo de espera ({@link ClusterNet#OPORTUNIDADES_RTT} * {@link ClusterNet#RTT})<br>
  * Cuando este tiempo es superado se ejecuta el callback registrado con el id_tpdu.<br>
  * Contiene instancias de {@link RegistroColaVencimiento}.
  * Los campos v�lidos de los registros que almacena son:
  * <ul>
  *    <li>{@link RegistroColaVencimiento#object}</li>
  *    <li>{@link RegistroColaVencimiento#id_tpdu}</li>
  *    <li>{@link RegistroColaVencimiento#lArg}</li>
  *    <li>{@link RegistroColaVencimiento#lTiempoFinal}</li>
  * </ul>
  */
  private LinkedList colaVencimientoRTT = null;


 /**
  * Cola con la lista de vencimientos de las funciones registradas para que
  * se ejecute el callback transcurrido un tiempo indicado.<br>
  * Contiene instancias de {@link RegistroColaVencimiento}.
  * Los campos v�lidos de los registros que almacena son:
  * <ul>
  *    <li>{@link RegistroColaVencimiento#object}</li>
  *    <li>{@link RegistroColaVencimiento#o}</li>
  *    <li>{@link RegistroColaVencimiento#lArg}</li>
  *    <li>{@link RegistroColaVencimiento#lTiempoFinal}</li>
  * </ul>
  */
  private LinkedList colaVencimientoFunciones = null;

 /**
  * Cola con la lista de vencimientos de las funciones peri�dicas registradas.<br>
  * Contiene instancias de {@link RegistroColaVencimiento}.
  * Los campos v�lidos de los registros que almacena son:
  * <ul>
  *    <li>{@link RegistroColaVencimiento#object}</li>
  *    <li>{@link RegistroColaVencimiento#o}</li>
  *    <li>{@link RegistroColaVencimiento#lArg}</li>
  *    <li>{@link RegistroColaVencimiento#lTiempoFinal}</li>
  *    <li>{@link RegistroColaVencimiento#lNPeriodos}</li>
  *    <li>{@link RegistroColaVencimiento#lTPeriodo}</li>
  * </ul>
  */
  private LinkedList colaVencimientoFuncionesPeriodicas = null;

 /**
  * Indica el n�mero de RTT pendientes.
  */
  private int iContador=0;

  /**
   * Instante de tiempo que marca cuando el clusterTimer queda inactivo.
   * Si vale cero, indica que no tiene que llamar m�s a la funci�n callbacks
   * de aviso de fin RTT.
   */
  private long lTiempoFinal=0;

  /**
   * Tiempo de finalizaci�n del siguiente RTT.
   * Cuando este tiempo es alcanzado se ejecuta la  funci�n callback registrada
   * con {@link #registrarFuncionRTT(TimerHandler)} o {@link #registrarFuncionRTT(TimerHandler,long)}.
   */
  private  long lTiempoSiguienteRTT = 0;

  /**
   * Variable que indica si ha sido inicilizado el clusterTimer.
   */
  private  boolean bInicializado=false;

  /**
   * Callback que se llama cada RTT (tiempo de RTT).
   */
  private  TimerHandler funcionRTT = null;

  /**
   * Thread utilizado para medir los tiempos, y ejecutar los callback.
   */
  private  ThreadTemporizador threadTemporizador = null;

  /**
   * Primer argumento de la funci�n callback registrada en
   * con {@link #registrarFuncionRTT(TimerHandler)} o {@link #registrarFuncionRTT(TimerHandler,long)}.
   */
  private  long lArgFuncionRTT;

  /**
   * M�ximo n�mero de RTT que pueden pasar por cada ID_TPDU registrado.
   */
  private  final int iINTENTOS = ClusterNet.OPORTUNIDADES_RTT + 1; // N�mero de intentos para dar por finalizado un NS.

  /**
   * Tiempo de RTT en milisegundos.
   */
  private  final long lRTT = ClusterNet.RTT; // Milisegundos.



  //NO OLVIDAR QUE LO QUE EJECUTE EL TEMPORIZADOR DEBER� SER M�NIMO


 //==========================================================================
 /**
  * Constructor.
  * @throws ErrorInitTempException
  */
 public ClusterTimer () throws ErrorInitTempException
 {
  this.iniciar ();
 }

 //==========================================================================
 /**
  * Registra un objeto TimerHandler cuyo m�todo ser�  ejecutado cada vez que
  * finalice el periodo indicado. Permite indicar el n�mero de periodos.<br>
  * El tiempo indicado debe ser mayor que cero.
  * Si el n�mero de periodos es menor o igual a cero, entonces se entiende que
  * son infinitos.
  * @param obj contiene el m�todo callback, al que se pasar� por argumento
  * 0 y null.
  * @param lTPeriodo milisegundos de duraci�n del periodo, tiene que ser mayor de cero.
  * @paran lNPeriodos n�mero de periodos durante los que tiene que ejecutar el
  * objeto (obj) registrado.
  * @see TimerHandler
  */
public  synchronized void registrarFuncionPeriodica (
                        TimerHandler obj,
                        long lTPeriodo,  //Expresado en mseg.
                        long lNPeriodos)
{
  registrarFuncionPeriodica (obj,lTPeriodo,lNPeriodos,0);
}


 //==========================================================================
 /**
  * Registra un objeto TimerHandler cuyo m�todo ser�  ejecutado cada vez que
  * finalice el periodo indicado. Permite indicar el n�mero de periodos.<br>
  * El tiempo indicado debe ser mayor que cero.
  * Si el n�mero de periodos es menor o igual a cero, entonces se entiende que
  * son infinitos.
  * @param obj contiene el m�todo callback, al que se pasar� por argumento
  * lArg y null.
  * @param lTPeriodo milisegundos de duraci�n del periodo, tiene que ser mayor de cero.
  * @paran lNPeriodos n�mero de periodos durante los que tiene que ejecutar el
  * objeto (obj) registrado.
  * @param lArg primer argumento del callback.
  * @see TimerHandler
  */
public  synchronized void registrarFuncionPeriodica (
                        TimerHandler obj,
                        long lTPeriodo,  //Expresado en mseg.
                        long lNPeriodos,
                        long lArg)
{
 final String mn = "ClusterTimer.registrarFuncionPeriodica (...)";
  long lTA;

  if (obj == null || lTPeriodo <= 0)
    return;


 try{
  if (!bInicializado)
       iniciar();
  } catch (ErrorInitTempException e)
        {
         Log.log (mn,e.toString());
         System.exit(0);
         }

  RegistroColaVencimiento registro = new RegistroColaVencimiento ();

  registro.object = obj;
  registro.lArg = lArg;
  registro.o = null;
  registro.id_tpdu = null;
  registro.lTPeriodo = lTPeriodo;
  if (lNPeriodos<0)
     registro.lNPeriodos = 0;
  else registro.lNPeriodos = lNPeriodos;

  lTA = System.currentTimeMillis();
  registro.lTiempoFinal=lTA+lTPeriodo;

  // Registrar Callbacks
  if (obj!=null)
        colaVencimientoFuncionesPeriodicas.addLast(registro);
}

 //==========================================================================
 /**
  * Elimina de la cola de vencimientos peri�dicos (@link #colaVencimientoFuncionesPeriodicas})
  * el primer registro encontrado cuyo timerHandler coincida con el indicado.
  * @param timerHandler timerHandler con el que compara los registrados.
  */
 public  synchronized void cancelarFuncionPeriodica (TimerHandler timerHandler)
 {
  cancelarFuncionPeriodica (timerHandler,0);
 }

 //==========================================================================
 /**
  * Elimina de la cola de vencimientos peri�dicos (@link #colaVencimientoFuncionesPeriodicas})
  * el primer registro encontrado cuyo timerHandler y lArg coincida con los
  * indicados
  * @param timerHandler
  * @param lArg
  */
 public  synchronized void cancelarFuncionPeriodica (TimerHandler timerHandler,
                                                           long lArg)
 {
  final String mn = "ClusterTimer.cancelarArg1 (long)";
  RegistroColaVencimiento registro;


 try{
  if (!bInicializado)
       iniciar();
  } catch (ErrorInitTempException e)
        {
         Log.log (mn,e.toString());
         System.exit(0);
         }

 for (int i=0; i<colaVencimientoFuncionesPeriodicas.size(); i++)
 {
   registro = (RegistroColaVencimiento) colaVencimientoFuncionesPeriodicas.get(i);

   if ( (registro.object == timerHandler) && (registro.lArg == lArg))
    {
         colaVencimientoFuncionesPeriodicas.remove (i);
         return ; // S�lo elimina la 1� ocurriencia.
    }
  } // Fin de for.

  comprobarColasVencimientos ();
 }



   //==========================================================================
  /**
   * Registra la funci�n que ser� llamada en cada vencimiento del tiempo de RTT.
   * @param obj Objeto que contiene la funcion callback que ser� llamada
   * cada vencimiento de RTT. Si vale null no se ejecutar� ninguna funci�n cada
   * vez que venza el RTT. Los par�metros que se le pasar�n son lArg y null.
   * @param lArg1 primer argumento de la funci�n callback
   * @see TimerHandler
   */
 public  synchronized void registrarFuncionRTT (TimerHandler obj,long lArg)
 {
   funcionRTT = obj;
   lArgFuncionRTT = lArg;
 }

  //==========================================================================
  /**
   * Registra la funci�n que ser� llamada en cada vencimiento del tiempo de RTT.
   * @param obj Objeto que contiene la funcion callback que ser� llamada
   * cada vencimiento de RTT. Si vale null no se ejecutar� ninguna funci�n cada
   * vez que venza el RTT. Los par�metros que se le pasar�n son 0 y null.
   * @see TimerHandler
   */
 public  synchronized void registrarFuncionRTT (TimerHandler obj)
 {
   final String mn = "ClusterTimer.registrarFuncionRTT (TimerHandler)";

   registrarFuncionRTT (obj,0);
 }


//==========================================================================
/**
 * Indica si hay una funci�n de aviso cada RTT registrada.
 * @return true en caso de que haya una funci�n registrada y false en otro caso.
 */
public  synchronized boolean registradaFuncionRTT ()
{
   return (funcionRTT==null);
}


  //==========================================================================
  /**
   * Cancela los avisos (ejecuci�n de callback) que se gener�n cada RTT (tiempo
   * de RTT). La cola de vencimientos de RTT ({@link #colaVencimientoRTT}) no se
   * es alterada, por lo que los callbacks registrados se ir�n ejecutando cuando
   * se alcace el tiempo final para los id_tpdu registrados.<br>
   * Para volver a activarlos se tiene que registrar la funcionRTT de nuevo.
   */
  public  synchronized void cancelarAvisoRTT ()
  {
   funcionRTT = null;
  }


 //===========================================================================
 /**
  * Indica por que parte de RTT vamos, es decir, indica un porcentaje (tantos
  * por ciento) sobre el RTT consumido hasta este momento.
  * @return 0 indica que no hay RTT registrados o que no ha transcurrido nada
  * del RTT actual. Si es mayor que cero, indica el tanto por ciento que ha
  * transcurrido del RTT actual.
  */
 public  synchronized int getPorcentajeRTTActual ()
 {

  if (iContador>0) // Se est� esperando por RTT
    {
     long lDiferencia = lTiempoSiguienteRTT - System.currentTimeMillis ();
     if (lDiferencia <= 0)
        return 100; // Se ha consumido el 100 % del RTT actual.
     if (lRTT > 0)
        return ( 100 - (int)((lDiferencia*100)/lRTT) ) ;
    }
  return 0;
 }


 //==========================================================================
 /**
  * <b>Registra un objeto TimerHandler cuyo m�todo ser� ejecutado en cada vencimiento
  * de RTT. </b>
  * @param obj contiene el m�todo callback, al que se pasar� por argumento
  * lArg y null.
  * @param lArg primer argumento del callback.
  */
 public  synchronized long registrarAvisoRTT (TimerHandler obj,long lArg)
 {
  return registrarAvisoRTT (obj,lArg,null);

 }

 //==========================================================================
 /**
  * Registra un nuevo id_tpdu para avisar cada RTT.
  * <p>A�ade una nueva entrada en la cola de vencimientos de RTT ({@link #colaVencimientoRTT}).
  * Cuando se alcance la espera m�xima ({@link ClusterNet#OPORTUNIDADES_RTT} * {@link ClusterNet#RTT})
  * para este id_tpdu se ejecuta el callback contenido en el objeto (obj).
  * @param obj contiene el m�todo que se ejecutar� cuando expire el <b>tiempo de espera
  * m�ximo</b> para el id_tpdu indicado, al que se pasar� por argumento lArg y
  * id_tpdu
  * @param lArg primer argumento que se le pasa a la funci�n callback.
  */
 public  synchronized long registrarAvisoRTT (TimerHandler obj,
                                                    long lArg,ID_TPDU id_tpdu)
 {
  final String mn = "ClusterTimer.registrarAvisoRTT (TimerHandler,arg,id_tpdu)";


  RegistroColaVencimiento registro = new RegistroColaVencimiento();
  long lTF,lTA;
  long lAux;


 try{
  if (!bInicializado)
       iniciar();
  } catch (ErrorInitTempException e)
        {
         Log.log (mn,e.toString());
         System.exit(0);
         }


  registro.object=obj;
  registro.lArg = lArg;
  registro.id_tpdu = id_tpdu;
  registro.o = id_tpdu;

  lAux=0;
  lTA = System.currentTimeMillis();
  lTF = lTA + iINTENTOS*lRTT;
  if ((iContador>0)&&(lTiempoFinal>0))
      {
      lAux=Math.round((lTF-lTiempoFinal)/(double)lRTT);
      iContador+=lAux;
      lTiempoFinal+=lAux*lRTT;
      }
    else {
           iContador = iINTENTOS;
           lTiempoFinal=lTF;
         }
  registro.lTiempoFinal=lTiempoFinal;
  if ((iContador>0)&&(lTiempoSiguienteRTT==0))
        lTiempoSiguienteRTT=lTA+lRTT;

  // Registrar Callbacks
  if (obj!=null)
        colaVencimientoRTT.addLast(registro);

  return lTA;
  }// Fin de registrarAvisoRTT ()


 //==========================================================================
 /**
  * Elimina de la cola de vencimientos de RTT ({@link #colaVencimientoRTT}) el
  * registro encontrado cuyo timerHandler y lArg coincida con el indicado.<br>
  * S�lo elimina la primera ocurrencia que encuentre.
  * @param timerHandler
  * @param lArg
  */
 public  synchronized void cancelarRTT (TimerHandler timerHandler,long lArg)
 {
  final String mn = "ClusterTimer.cancelarRTT (timerHandler,lArg)";
  RegistroColaVencimiento registro;

  try{
   if (!bInicializado)
       iniciar();
   } catch (ErrorInitTempException e)
        {
         Log.log (mn,e.toString());
         System.exit(0);
         }

  /*
    La colaVencimiento est� ordenada crecientemente por el n�mero de secuencia.
  */
  for (int i=0; i<colaVencimientoRTT.size(); i++)
  {
   registro = (RegistroColaVencimiento) colaVencimientoRTT.get(i);

   if ( (registro.object == timerHandler) && (registro.lArg == lArg))
    {
         colaVencimientoRTT.remove (i);
         return ; // S�lo elimina la 1� ocurriencia.
    }
   } // Fin de for.

  comprobarColasVencimientos ();
 }



 //==========================================================================
 /**
  * Elimina de la cola de vencimientos de RTT ({@link #colaVencimientoRTT})
  * los registros encontrados cuyo timerHandler sea el indicado, y cuyo segundo
  * argumento sea un id_tpdu igual al pasado por argumento.<br>
  * S�lo elimina la primera ocurrencia.
  * @param timerHandler timerHandler con el que compara los registrados en
  * ({@link #colaVencimientoRTT}).
  * @param id_TPDU id_tpdu con el que compara los registrados en ({@link #colaVencimientoRTT}).
  */
 public synchronized void cancelarRTTID_TPDU (TimerHandler timerHandler,
                                                  ID_TPDU id_TPDU)
 {
  final String mn = "ClusterTimer.cancelarRTTID_TPDU (timerHandler,id_TPDU)";
  RegistroColaVencimiento registro;


 try{
  if (!bInicializado)
       iniciar();
  } catch (ErrorInitTempException e)
        {
         Log.log (mn,e.toString());
         System.exit(0);
         }


 for (int i=0; i<colaVencimientoRTT.size(); i++)
 {
   registro = (RegistroColaVencimiento) colaVencimientoRTT.get(i);

   if ( (registro==null) || (registro.id_tpdu==null) )
      continue;

   if ((registro.object==timerHandler)&&(registro.id_tpdu.equals (id_TPDU)))
    {
         colaVencimientoRTT.remove (i);
         return ; // S�lo elimina la 1� ocurriencia.
    }
  } // Fin de for.


  comprobarColasVencimientos ();

} // Fin del cancelarID_TPDU(id_tpdu)



 //==========================================================================
 /**
  * Elimina de la cola de vencimientos de RTT ({@link #colaVencimientoRTT})
  * los registros encontrados cuyo timerHandler sea el indicado, y cuyo segundo
  * argumento sea un id_tpdu con id_socket igual y n�mero de secuencia menor o
  * igual al id_TPDU indicado en el argumento.
  * @param timerHandler timerHandler con el que compara los registrados en
  * ({@link #colaVencimientoRTT}).
  * @param id_TPDU id_tpdu con el que compara los registrados en ({@link #colaVencimientoRTT}).
  */
 public  synchronized void cancelarRTTID_TPDUMenorIgual (
                                                      TimerHandler timerHandler,
                                                      ID_TPDU id_TPDU)
 {
  final String mn = "ClusterTimer.cancelarRTTID_TPDUMenorIgual (timerHandler,id_tpdu)";
  RegistroColaVencimiento registro;

 try{
  if (!bInicializado)
       iniciar();
  } catch (ErrorInitTempException e)
        {
         Log.log (mn,e.toString());
         System.exit(0);
         }


  if (id_TPDU==null)
     return;

  for (int i=(colaVencimientoRTT.size()-1); i>=0; i--)
  {
   registro = (RegistroColaVencimiento) colaVencimientoRTT.get(i);


   if ((registro==null)||(registro.id_tpdu == null))
      continue;

   if (registro.object!=timerHandler)
      continue;

   if (!registro.id_tpdu.getID_Socket().equals (id_TPDU.getID_Socket()))
      continue;


   if (registro.id_tpdu.getNumeroSecuencia().compareTo(
                                           id_TPDU.getNumeroSecuencia())<=0)
      {
         colaVencimientoRTT.remove (i);
       }

  } // Fin de for.
  comprobarColasVencimientos ();
 }


 //==========================================================================
 /**
  * Registra un objeto TimerHandler cuyo m�todo ser�  ejecutado cuando se alcance
  * el tiempo indicado.<br>
  * El tiempo indicado debe ser mayor que cero.
  * @param obj contiene el m�todo callback, al que se pasar�n por argumento
  * 0 y null.
  * @param lMseg milisegundos.
  * @see TimerHandler
  */
 public  synchronized void registrarFuncion (TimerHandler obj,long lMseg)
 {
  final String mn = "ClusterTimer.registrarFuncion (TimerHandler,mseg)";

  registrarFuncion (lMseg,obj,0,null);

  }// Fin de registrarFuncion ()

 //==========================================================================
 /**
  * Registra un objeto TimerHandler cuyo m�todo ser�  ejecutado cuando se alcance
  * el tiempo indicado.<br>
  * El tiempo indicado debe ser mayor que cero.
  * @param lMseg milisegundos.
  * @param obj contiene el m�todo callback, al que se pasar� por argumento
  * lArg y null.
  * @param lArg primer argumento del callback
  * @see TimerHandler
  */
 public  synchronized void registrarFuncion (long lMseg,TimerHandler obj,
                                                   long lArg)
 {
  registrarFuncion (lMseg,obj,lArg,null);
 }// Fin de registrarFuncion ()

 //==========================================================================
 /**
  * Registra un objeto TimerHandler cuyo m�todo ser�  ejecutado cuando se alcance
  * el tiempo indicado.<br>
  * El tiempo indicado debe ser mayor que cero.
  * @param lMseg milisegundos.
  * @param obj contiene el m�todo callback, al que se pasar� por argumento
  * lArg y null.
  * @param lArg primer argumento del callback
  * @param o segundo argumento del callback
  * @see TimerHandler
  */
 public  synchronized void registrarFuncion (long lMseg,TimerHandler obj,
                                                   long lArg,
                                                   Object o)
 {
  final String mn = "ClusterTimer.registrarFuncion (lMseg,timerHandler,lArg,obj)";
  RegistroColaVencimiento registro = new RegistroColaVencimiento();
  long lTA;

  if (lMseg<=0)
        return;

 try{
  if (!bInicializado)
       iniciar();
  } catch (ErrorInitTempException e)
        {
         Log.log (mn,e.toString());
         System.exit(0);
         }

  registro.object  = obj;
  registro.lArg    = lArg;
  registro.o       = o;
  registro.id_tpdu = null;

  lTA = System.currentTimeMillis();
  registro.lTiempoFinal=lTA+lMseg;

  // Registrar Callbacks
  if (obj!=null)
        colaVencimientoFunciones.addLast(registro);


  }// Fin de registrarFuncion ()


 //==========================================================================
 /**
  * Elimina de la cola de vencimientos peri�dicos (@link #colaVencimientoFunciones})
  * el primer registro encontrado cuyo timerHandler y lArg coincida con los
  * indicados
  * @param timerHandler
  * @param lArg
  */
 public  synchronized void cancelarFuncion (TimerHandler timerHandler,long lArg)
 {
  final String mn = "ClusterTimer.cancelarFuncion (timerHandler,lArg)";
  RegistroColaVencimiento registro;

  try{
   if (!bInicializado)
       iniciar();
   } catch (ErrorInitTempException e)
        {
         Log.log (mn,e.toString());
         System.exit(0);
         }
  for (int i=0; i<colaVencimientoFunciones.size(); i++)
  {
   registro = (RegistroColaVencimiento) colaVencimientoFunciones.get(i);

   if ( (registro.object == timerHandler) && (registro.lArg == lArg))
    {
         colaVencimientoFunciones.remove (i);
         return ; // S�lo elimina la 1� ocurriencia.
    }
  } // Fin de for.

  comprobarColasVencimientos ();
 }



//==========================================================================
/**
 * Funci�n que s�lo se ejecuta una vez. Inicia el hilo encargado de medir
 * el tiempo y crea las colas de vencimientos.
 * @throws ErrorInitTempException Excepci�n lanzada si hay un error al
 * iniciar el clusterTimer.
 */
private  void iniciar () throws ErrorInitTempException
 {
  final String mn = "ClusterTimer.iniciar";

  if (colaVencimientoRTT == null)
     colaVencimientoRTT = new LinkedList ();
  else throw new ErrorInitTempException ();

  if (colaVencimientoFunciones == null)
     colaVencimientoFunciones = new LinkedList ();
  else throw new ErrorInitTempException ();

  if (colaVencimientoFuncionesPeriodicas == null)
     colaVencimientoFuncionesPeriodicas = new LinkedList ();
  else throw new ErrorInitTempException ();

  if (threadTemporizador==null)
    {
     threadTemporizador = new ThreadTemporizador (this);
     if (threadTemporizador!=null)
        threadTemporizador.start();
     else throw new ErrorInitTempException ();
     }
  bInicializado = true;
 }

  //==========================================================================
  /**
   * Duerme al thread que lo ejecute durante los milisegundos indicados.
   * @param mseg milisegundos de espera.
   */
   public static void sleep(long lMseg)
   {
    if (lMseg<0)
       return;

    try
     {
      Thread.sleep(lMseg);
     }
      catch (InterruptedException e)
      { }
    }

  //==========================================================================
  /**
   * Devuelve el tiempo actual, expresado  en milisegundos.
   */
   public static long tiempoActualEnMseg ()
   {
    return System.currentTimeMillis ();
   }

  //==========================================================================
  /**
   * Cede el procesador a cualquier otra tarea que este esperando.
   */
   public static void yield()
   {
      Thread.yield();
   }


 //==========================================================================
 /**
  * Cancela todas las operaciones del clusterTimer hasta que no se vuelvan a
  * registrar m�s callbacks.
  */
 public synchronized void cancelarTodoTemporizador ()
 {
    if (colaVencimientoRTT!=null)
        colaVencimientoRTT.clear();
    if (colaVencimientoFunciones!=null)
        colaVencimientoFunciones.clear();
    if (colaVencimientoFuncionesPeriodicas!=null)
        colaVencimientoFuncionesPeriodicas.clear();

    iContador = 0;
    lTiempoFinal = 0;
    lTiempoSiguienteRTT = 0;
 }



 //==========================================================================
 /**
  * Comprueba si las colas han quedado vac�as y actualiza variables.
  */
  private  synchronized void comprobarColasVencimientos ()
  {
   if (colaVencimientoRTT.size()==0 && colaVencimientoFunciones.size()==0
        && colaVencimientoFuncionesPeriodicas.size()==0)
          cancelarTodoTemporizador ();
   else if (colaVencimientoRTT.size ()==0)
             { // Cancelar el contador, es decir, no se tienen que generar m�s RTT
              iContador = 0;
              lTiempoFinal = 0;
              lTiempoSiguienteRTT = 0;
             }
  }


 //==========================================================================
 /**
  * Reinicia el clusterTimer.
  */
 public  synchronized void reiniciar ()
 {
   final String mn = "ClusterTimer.reiniciar";

   try{
    if (!bInicializado)
        iniciar();
    } catch (ErrorInitTempException e)
        {
         Log.log (mn,e.toString());
         System.exit(0);
         }

   cancelarTodoTemporizador ();
 }

 //==========================================================================
 /**
  * Busca el siguiente objeto callback cuyo tiempo final es igual o menor al
  * tiempo indicado. Esta funci�n controla si se ha alcanzado el tiempo de
  * espera del siguiente RTT o ha finalizado el tiempo para alg�n n�mero de
  * secuencia registrado en la colas de vencimientos.
  * @param lTiempo tiempo en milisegundos utilizado para comparar con los
  * registrados.
  */
  synchronized boolean buscarSiguiente (long lTiempo)
 {
  RegistroColaVencimiento  elemento=null;


 // Comprueba si ha vencido el tiempo m�ximo de espera de alg�n n�mero de
 // secuencia registrado en la cola de vencimientos.
 for (int i=0;i<colaVencimientoRTT.size();i++)
 {
   elemento = (RegistroColaVencimiento) colaVencimientoRTT.get(i);
   if (lTiempo>elemento.lTiempoFinal)
        {
         threadTemporizador.objeto = elemento.object;
         threadTemporizador.lCallbackArg = elemento.lArg;
         threadTemporizador.callbackObject = elemento.id_tpdu;
         colaVencimientoRTT.remove (i);
         if (colaVencimientoRTT.size()==0)
          {
           if (colaVencimientoFunciones.size()==0)
             cancelarTodoTemporizador ();
           else { // Cancelar el contador, es decir, no se tienen que generar m�s RTT
                 iContador = 0;
                 lTiempoFinal = 0;
                 lTiempoSiguienteRTT = 0;
                }
          }
         return (true);
        }
   } // Fin de for.

  // Comprueba si ha vencido RTT.
  if ((iContador>0)&&(lTiempo>= lTiempoSiguienteRTT))
    {
     threadTemporizador.objeto = funcionRTT;
     threadTemporizador.lCallbackArg = lArgFuncionRTT;
     threadTemporizador.callbackObject = null;
     iContador--;
     if (iContador>0)
       lTiempoSiguienteRTT+=lRTT;
     else {
           lTiempoSiguienteRTT=0;
           lTiempoFinal=0;
           }
     return (true);
    }

 // Comprueba si ha vencido el tiempo m�ximo de espera de alg�n n�mero de
 // secuencia registrado en la cola de vencimientos.
 for (int i=0;i<colaVencimientoFunciones.size();i++)
 {
   elemento = (RegistroColaVencimiento) colaVencimientoFunciones.get(i);
   if (lTiempo>elemento.lTiempoFinal)
        {
         threadTemporizador.objeto = elemento.object;
         threadTemporizador.lCallbackArg = elemento.lArg;
         threadTemporizador.callbackObject = elemento.o;
         colaVencimientoFunciones.remove (i);
         return (true);
        }
   } // Fin de for.*/

 // Comprueba las funciones peri�dicas registradas.
 for (int i=0;i<colaVencimientoFuncionesPeriodicas.size();i++)
 {
   elemento = (RegistroColaVencimiento) colaVencimientoFuncionesPeriodicas.get(i);
   if (lTiempo>elemento.lTiempoFinal)
        {
         threadTemporizador.objeto = elemento.object;
         threadTemporizador.lCallbackArg = elemento.lArg;
         threadTemporizador.callbackObject = elemento.o;
         if (elemento.lNPeriodos==1)
           colaVencimientoFuncionesPeriodicas.remove (i); // Eliminar
         else // Actualizar el nPeriodos (Es 0 o mayor que 1)
              {
               // � O LE SUMO tiempo actual (variable tiempo)??
               elemento.lTiempoFinal += elemento.lTPeriodo; // Incrementar en periodo
               if (elemento.lNPeriodos > 0) //
                  elemento.lNPeriodos --;
              }
         return (true);
        }
   } // Fin de for.*/


  return (false);
 }

  //==========================================================================
  /**
   * Devuelve una cadena informativa.
   */
  public String toString ()
  {
   return "";//colaVencimientoRTT.toString () + colaVencimientoFunciones.toString();
  }


}// Fin clase ClusterTimer.


//==================================================================
//           CLASE     RegistroColaVencimiento
//==================================================================


/**
 * Almacena la informaci�n sobre cuando tiene que ejecutarse un callback
 * y con que argumentos.<br>
 * Clase que contiene un elemento de la cola de vencimientos.
 * @see ClusterTimer#colaVencimientoFunciones
 * @see ClusterTimer#colaVencimientoFuncionesPeriodicas
 * @see ClusterTimer#colaVencimientoRTT
 * @version 1.0
 * @author M. Alejandro Garc�a Dom�nguez.
 *  Antonio Berrocal Piris.
 */
class RegistroColaVencimiento
{
  // ATRIBUTOS
  /**
   * Objeto conteniendo la funci�n callback, que ser� ejecutada cuando sea
   * alcanzado el tiempoFinal.
   */
  public TimerHandler object = null;

  /**
   * Instante de tiempo a partir del cual este n�mero de secuencia es retirado de
   * la cola de vencimientos porque se le ha agotado el tiempo de espera.
   */
  public long lTiempoFinal;

  /** Primer argumento de la funci�n callback. */
  public long lArg;

  /** Segundo argumento de la funci�n callback*/
  public Object o;

  /** Segundo argumento de la funci�n callback, cuando lo que se registro fue
    * un id_tpdu para avisar al finalizar las oportunidades ({@link ClusterTimer#iINTENTOS})
    **/
  public ID_TPDU id_tpdu;

  /** Tiempo Periodo */
  public long lTPeriodo = 0;

  /** M�mero de periodos */
  public long lNPeriodos = 1;


  //==========================================================================
  /**
   * Devuelve una cadena informativa.
   */
  public String toString ()
  {
   return "TimerHandler: " + this.object +
          " arg1: " + this.lArg +
          " o: " + this.o +
          " ID_TPDU: " + this.id_tpdu
          ;
   }

} // Fin de la clase RegistroColaVencimiento.


//==================================================================
//           CLASE     ThreadTemporizador
//==================================================================


/**
 * Clase que implementa un thread encargado de medir el tiempo.
 * @see ClusterTimer
 * @version 1.0
 * @author M. Alejandro Garc�a Dom�nguez.
 * Antonio Berrocal Piris.
 */
class ThreadTemporizador extends Thread
{
  /**
   * Objeto clusterTimer asociado al thread.
   */
  private ClusterTimer clusterTimer = null;

  //==========================================================================
  /**
   * Crea el thread.
   * @param temporizadorParam clusterTimer que utiliza el thread.
   */
 public ThreadTemporizador (ClusterTimer temporizadorParam)
 {
   super();

   this.clusterTimer = temporizadorParam;

   setDaemon(true);
   }

  //==========================================================================
  /**
   * M�todo run que ejecuta un bucle infinito que mide el tiempo. LLama a la
   * funci�n {@link ClusterTimer#buscarSiguiente(long)} para averiguar los
   * si tiene que ejecutar alg�n callback, y si es as�, lo ejecuta.
   */
 public void run ()
 {
  long lTiempo;
  boolean bReintentar;
  final String mn = "ThreadTemporizador.run";


   while (true)
    {
     try
     {
      sleep(50); // Espera 50 milisegundos.
      }
      catch (InterruptedException e)
      {}

     lTiempo = System.currentTimeMillis();  // Obtiene el tiempo actual.

     bReintentar = true;

     while (bReintentar)
     {
      bReintentar = clusterTimer.buscarSiguiente(lTiempo);
      if (bReintentar&&(objeto!=null))
        {
         objeto.TimerCallback (lCallbackArg,callbackObject); // Ejecuta la funci�n callback
        }
     }
    }
 }

// ATRIBUTOS

 /**
  * Objeto que contiene la funci�n callback que se tiene que ejecutar.
  */
 public TimerHandler objeto = null;

 /**
  * Primer argumento de la funci�n callback.
  */
 public long lCallbackArg = 0;

 /**
  * Primer argumento de la funci�n callback.
  */
 public Object callbackObject;

} // Fin de clase ThreadTemporizador




