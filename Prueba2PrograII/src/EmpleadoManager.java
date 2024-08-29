/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author dennisse
 */
public class EmpleadoManager {

    private RandomAccessFile rcods, remps;

    public EmpleadoManager() {
        try {
            // 1. Asegurar que el folder Company exista
            File mf = new File("company");
            mf.mkdir();

            //2. Instanciar los RAFs dentro del folder company
            rcods = new RandomAccessFile("company/codigos.emp", "rw");
            remps = new RandomAccessFile("company/empleados.emp", "rw");
            initCodes();
            //3. Inicializar el archivo de codigos, si, es nuevo
        } catch (IOException e) {
            System.out.println("Error");

        }
    }

    /*
    Formato Codigos.emp
    int code;
     */
    private void initCodes() throws IOException {
        //Cotejar el tamano del archivo
        if (rcods.length() == 0) {
            rcods.writeInt(1);
        }

    }

    /*
    Crear funcion getCode, para generar el codigo siguiente e indicarme cual es el codigo actual 
     */
    private int getCode() throws IOException {

        rcods.seek(0);

        int codigo = rcods.readInt();

        rcods.seek(0);
        rcods.writeInt(codigo + 1);

        return codigo;
    }

    /*
    Formato Empleado.emp
    
    int code
    String name
    double salary
    long fechaContratacion
    long fechaDespido
     */
    public void addEmployee(String name, double monto) throws IOException {

        remps.seek(remps.length());

        int code = getCode();

        remps.writeInt(code);
        remps.writeUTF(name);
        remps.writeDouble(monto);

        remps.writeLong(Calendar.getInstance().getTimeInMillis());
        remps.writeLong(0);

        //Crear carpeta y archivo individual de cada empleado
        createEmployeeFolders(code);
    }

    private String employeeFolder(int code) {
        return "company/empleado" + code;

    }

    private void createEmployeeFolders(int code) throws IOException {
        File edir = new File(employeeFolder(code));
        edir.mkdir();
        //Crear los archivos del empleado
        this.createYearSalesFileFor(code);

    }

    private RandomAccessFile saleFileFor(int code) throws IOException {
        String dirPadre = employeeFolder(code);
        int yearActual = Calendar.getInstance().get(Calendar.YEAR);
        String path = dirPadre + "/Ventas" + yearActual + ".emp";
        return new RandomAccessFile(path, "rw");
        

    }

    /*
    Formato VentasYear.emp
    double ventas (por mes)
    boolean estadoPagar
     */
    private void createYearSalesFileFor(int code) throws IOException {

        // Obtener el archivo de ventas para el año actual
        RandomAccessFile raf = saleFileFor(code);
        if (raf.length() == 0) {

            for (int mes = 0; mes < 12; mes++) {
                raf.writeDouble(0.0);
                raf.writeBoolean(false);

            }

            raf.close();

        }
    }

    public void imprimirEmpleadosActivos() throws IOException {

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        remps.seek(0);
        System.out.println("**** LISTA DE EMPLEADOS ****");
        while (remps.getFilePointer() < remps.length()) {
            int code = remps.readInt();
            String name = remps.readUTF();
            double salary = remps.readDouble();
            long fechaContratacion = remps.readLong();
            long fechaDespido = remps.readLong();

            if (fechaDespido == 0) {
                System.out.println(String.format("%d- %s - %.2f Lps - Fecha Contratación(%s)",
                        code, name, salary, formato.format(new Date(fechaContratacion))));
            }
        }
    }

    public boolean EmpleadosActivos(int code) throws IOException {
        remps.seek(0);

        while (remps.getFilePointer() < remps.length()) {
            int codigo = remps.readInt();

            if (codigo == code) {
                remps.readUTF();
                remps.readDouble();
                remps.readLong();
                long fechaDespido = remps.readLong();

                return fechaDespido == 0;
            } else {
                remps.readUTF();
                remps.readDouble();
                remps.readLong();
                remps.readLong();
            }
        }
        return false;
    }

    public boolean DespedirEmpleados(int code) throws IOException {
        remps.seek(0);

        while (remps.getFilePointer() < remps.length()) {
            int codigo = remps.readInt();
            String name = remps.readUTF();
            remps.readDouble();
            remps.readLong();
            long fechaDespido = remps.readLong();

            if (codigo == code) {
                if (fechaDespido != 0) {
                    return false;
                }
                remps.seek(remps.getFilePointer() - 8);
                remps.writeLong(Calendar.getInstance().getTimeInMillis());
                System.out.println("Empleado despedido: " + name);
                return true;
            }
        }
        return false;
    }

}
