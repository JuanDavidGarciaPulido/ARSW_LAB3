package edu.eci.arsw.blacklistvalidator;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import edu.eci.arsw.spamkeywordsdatasource.HostBlacklistsDataSourceFacade;

public class HostBlackListsThreats extends Thread {

    private static AtomicInteger occurrencias = new AtomicInteger(0);
    private int contadorListas;
    private String ip;
    private int inicio;
    private int fin;
    private static LinkedList<Integer> ListaOcurrencias = new LinkedList<>();
    private static volatile boolean stop = false;

    public HostBlackListsThreats(String ip, int inicio, int fin){
        this.ip = ip;
        this.inicio = inicio;
        this.fin = fin;
    }

    public void run() {
        this.chequeo(ip, inicio, fin);
    }

    public void chequeo(String ip, int inicio, int fin){
        HostBlacklistsDataSourceFacade skds = HostBlacklistsDataSourceFacade.getInstance();
        for (int i = inicio; i < fin; i++){
            contadorListas++;

            if (skds.isInBlackListServer(i, ip)){
                
                synchronized (ListaOcurrencias) {
                    ListaOcurrencias.add(i);
                }
                int currentOccurrences = occurrencias.incrementAndGet();
                if (currentOccurrences >= HostBlackListsValidator.BLACK_LIST_ALARM_COUNT) {
                    stop = true;
                }
            }
        }
    }

    public List<Integer> getListaOcurrencias(){
        synchronized (ListaOcurrencias) {
            return new LinkedList<>(ListaOcurrencias);
        }
    }

    public static AtomicInteger getOcurrencias(){
        return occurrencias;
    }

    public int getChequeoListasNegras(){
        return contadorListas;
    }

    public static void reset() {
        occurrencias.set(0);
        synchronized (ListaOcurrencias) {
            ListaOcurrencias.clear();
        }
        stop = false;
    }
}