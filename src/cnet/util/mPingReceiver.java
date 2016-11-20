package cnet.util;

import java.net.*;
import java.io.*;

import javax.swing.JOptionPane;

import cnet.*;

import javax.swing.Icon;

import java.util.*;
import java.text.*;


/**
 * <p>Title: mPingReceiver</p>
 *
 * <p>Description: Test tool multicast</p>
 * This program listen for ping packets send for mPingSender at a given multicast address and port.
 * <p>Copyright: Copyright (c) 2016</p>
 *
 * <p>Company: </p>
 *
 * @author Alejandro Garcia
 * @version 1.0
 */
public class mPingReceiver {


/** Tama�o del array de Transmisi�n/Recepcion */
public static final int TAMA�O_ARRAY_BYTES = 1024 * 2;


/** Nueva L�nea */
private static final String newline = "\n";

/** Direcci�n Multicast:puerto*/
private Address dirIPMcast = null;

/**TTL sesion */
private int TTLSesion = 8;


/** Socket Multicast Fiable*/
private MulticastSocket socket = null;

/** Flag de inicio del thread */
private boolean runFlag = true;

/** Ratio ed transferencia */
private long lRatio = 0;


//==========================================================================
/**
* Constructor
*/
public mPingReceiver(String dirIPMulticast, String puerto) throws IOException
{

System.out.println("mPingReceiver v1.1");
System.out.println("(c)2016 cnet // Alejandro Garcia");
System.out.println("------------------------------------------");


//Obtener par�metros
this.dirIPMcast = new Address(dirIPMulticast,Integer.parseInt(puerto));

}


//==========================================================================
/**
* M�todo Run()
*/
public void run()
{


try
{


    Date today;
    String output;
    SimpleDateFormat formatter;

    formatter = new SimpleDateFormat("yyyy.MM.dd '/' HH:mm:ss");
    today = new Date();
    output = formatter.format(today);
    System.out.println(output);



        //Crear el socket multicast
    socket = new MulticastSocket(dirIPMcast.getPort());
    Log.log("Socket opened"," OK");

    //Join
    socket.joinGroup(dirIPMcast.getInetAddress());
    Log.log("Join"," "+dirIPMcast.getHostAddress()+":"+dirIPMcast.getPort());

    //Buffer de recepcion
    socket.setReceiveBufferSize(1024*64);

    //SoTimeout
    socket.setSoTimeout(5000);
    Log.log("SoTimeout",""+socket.getSoTimeout());

     byte[] buf = new byte[this.TAMA�O_ARRAY_BYTES];
     DatagramPacket recv = new DatagramPacket(buf, buf.length);
     int puerto = 0;
     int size = 0;
     int contador = 0;
     String sMensaje = null;
     String sContador = null;
     String[] sTokens = null;

     int iPing = 0;
     int iContador = 0;
     boolean bPRimero = true;

    while(true)
    {
     try
     {
        //Leer datos...
        socket.receive(recv);
        puerto = recv.getPort();
        size = recv.getLength();
        if(size > 0)
        {
            sMensaje = new String(recv.getData(),0,size);
            //sTokens = sMensaje.split(" ");
            //sContador = sTokens[2]; //obtiene el contado
            //iPing = Integer.parseInt(sContador);
        }

        Address add = new Address(recv.getAddress(),puerto);
        contador =contador+1;
       //Log.log("Paquete "+contador," << "+add.getHostAddress()+":"+dirIPMcast.getPort()+ " size:"+size+" ** "+sMensaje+" / "+iPing);
       // Log.log(""+contador,sMensaje);
        Log.log(""+contador,"Received mping "+sMensaje+"   \t"+"<-- from "+add.getHostAddress()+":"+dirIPMcast.getPort());

      /*  //Comprobar si el ping llega en secuencia
        if(((iContador+1)%100 != iPing) && !bPRimero)
        {
            bPRimero = false;
            today = new Date();
            output = formatter.format(today);
            Log.log("*LOSS*",output+" lost: "+(iPing-iContador-1));
            logger("[mPingReceiver]: *MULTICAST LOSS* : "+(iPing-iContador-1));

        }
*/

        iContador = iPing;
     }
     catch(SocketTimeoutException z)
     {
        today = new Date();
         output = formatter.format(today);
         Log.log("*LOST?*",output+" No packets received by 5 seg!");
         //logger("[mPingReceiver]: *MULTICAST LOST?*  No packets received by 5 seg");
     }

    }


}
catch(IOException e)
{

 error(e.getMessage());
}

//Limpiar....
finally
{
  //Cerrar el Socket
  close();

}
}




//==========================================================================
/**
* Cerrar el Socket
*/
void close()
{
try
{
  socket.leaveGroup(dirIPMcast.getInetAddress());
  socket.close();
}
catch(IOException e)
{
   error(e.getMessage());
}
}


/**
* M�todo main.
* @param args
*/
public static void main(String[] args)
{
//Comprobar par�metros
if(args.length != 2)
{
  uso();
  return;
}

try
{
    mPingReceiver mescucha = new mPingReceiver(args[0],args[1]);
    mescucha.run();
}
catch(IOException io)
{
  System.out.print(io.getMessage());
}
}




//==========================================================================
/**
* Resumen
*/
private void resumenTransferencia(long lTiempoInicio, long lBytesTransmitidos)
{
  long lHoras = 0;
  long lMinutos = 0;
  long lSegundos = 0;
  long lMSegundos = 0;
  long lTiempo = System.currentTimeMillis() - lTiempoInicio;
  double dKB_seg =0;

  String mensaje = "Transmitted "+lBytesTransmitidos+" bytes en ";

  dKB_seg = ((double)(lBytesTransmitidos)/(double)(lTiempo)) *1000;
  dKB_seg = (dKB_seg / 1024);

  if (lTiempo > 1000)
  {
    //Calcular Horas
    lHoras = ((lTiempo/1000)/60)/60;
    lMinutos = ((lTiempo/1000)/60)%60;
    lSegundos = ((lTiempo/1000)%60);
    lMSegundos = (lTiempo%1000);

    //Establecer el tiempo.....
    if(lHoras > 0)
      mensaje+=(lHoras+" hr. "+lMinutos+" min.");
    else if(lMinutos > 0)
      mensaje+=(lMinutos+" min. "+lSegundos+" seg.");
    else
      mensaje+=(lSegundos+" seg."+lMSegundos+" mseg.");
  }
  else
      mensaje+=(lTiempo+" mseg.");


  System.out.println("");
  System.out.println("");

  if (dKB_seg > 1)
  {
    int iParteEntera = (int)(dKB_seg );
    int iParteDecimal = (int)(dKB_seg *100)%100;
   // Log.log(mn,mensaje);
   // Log.log(mn,"Ratio Transferencia: "+iParteEntera+"."+iParteDecimal+" KB/Seg");
  }
  else
  {
    int i = (int)(dKB_seg * 100);
    //Log.log(mn,mensaje);
    //Log.log(mn,"Ratio Transferencia: 0."+i+" KB/Seg");
  }

}






/**
* Imprime el mensaje de uso de la aplicaci�n
*/
private static void uso()
{
System.out.println("");
System.out.println("mPingReceiver v1.1" );
System.out.println("Uso: java cnet.ping.mPingReceiver <multicast address> <port>");
System.out.println("");
System.out.println("");
}


/**
* Imprime el mensaje de error
* @param sError
*/
private void error(String sError)
{
System.out.println("=========================================");
System.out.print("Error: ");
System.out.println(sError);

}



    /**
     * Manda el mensaje al logger de consola!!!
     * @param <any> String
     */
    private void logger(String log)
    {
               //Llamar a logger
               try
               {
                   Runtime rt = Runtime.getRuntime() ;
                   String mensaje = "logger "+log;
                   //Log.log("logger",mensaje);
                   String comando[] = { "logger", log };
                   Process p = rt.exec(comando) ;
                   InputStream in = p.getInputStream() ;
                   OutputStream out = p.getOutputStream ();
                   InputStream err = p.getErrorStream() ;


                   //do whatever you want
                   //some more code
                   //p.wait(2000);
                   p.waitFor();
                   //log("----------------------------------------------------------------------");


               }catch(Exception exc )
               {
                   /*handle exception*/
                   System.err.println(exc.getMessage());
               }

    }

}



