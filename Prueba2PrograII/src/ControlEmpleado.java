/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 *
 * @author dennisse
 */
public class ControlEmpleado {

    public static void main(String[] args) {
        EmpleadoManager manager = new EmpleadoManager();
        Scanner lea = new Scanner(System.in);
        int opcion = 0;

        do {
            System.out.println("--- MENÚ ---");
            System.out.println("1. Agregar Empleado");
            System.out.println("2. Listar Empleados No Despedidos");
            System.out.println("3. Despedir Empleado");
            System.out.println("4. Buscar Empleado Activo");
            System.out.println("5. Salir");
            System.out.print("Elija una opción: ");

            try {
                opcion = lea.nextInt();
            } catch (InputMismatchException e) {
                System.out.println("Por favor, ingrese un número válido.");
                lea.next();
                continue;
            }

            switch (opcion) {
                case 1:
                    agregarEmpleado(manager, lea);
                    break;
                case 2:
                    try {
                    manager.imprimirEmpleadosActivos();
                } catch (IOException e) {
                    System.out.println("Error al listar empleados: " + e.getMessage());
                }
                break;
                case 3:
                    despedirEmpleado(manager, lea);
                    break;
                case 4:
                    buscarEmpleadoActivo(manager, lea);
                    break;
                default:
                    System.out.println("Opción no válida, ingrese un número del 1 al 4.");
            }
        } while (opcion != 5);

        lea.close();
    }

    private static void agregarEmpleado(EmpleadoManager manager, Scanner lea) {
        lea.nextLine();
        System.out.print("Ingrese el nombre del empleado: ");
        String nombre = lea.nextLine();
        double salario = 0;
        boolean entradaValida = false;

        while (!entradaValida) {
            System.out.print("Ingrese el salario del empleado (mayor a 0): ");
            try {
                salario = lea.nextDouble();
                if (salario > 0) {
                    entradaValida = true;
                } else {
                    System.out.println("Error, el salario debe ser mayor a cero, intente de nuevo.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Error: Por favor, ingrese un número válido para el salario.");
                lea.next();
            }
        }

        try {
            manager.addEmployee(nombre, salario);
            System.out.println("Empleado agregado exitosamente.");
        } catch (IOException e) {
            System.out.println("Error al agregar empleado: " + e.getMessage());
        }
    }

    private static void despedirEmpleado(EmpleadoManager manager, Scanner lea) {
        System.out.print("Ingrese el código del empleado a despedir: ");
        int codigo = lea.nextInt();

        try {
            if (manager.DespedirEmpleados(codigo)) {
                System.out.println("Empleado despedido con éxito.");
            } else {
                System.out.println("El empleado ya fue despedido o no existe.");
            }
        } catch (IOException e) {
            System.out.println("Error al despedir empleado: " + e.getMessage());
        }
    }

    private static void buscarEmpleadoActivo(EmpleadoManager manager, Scanner lea) {
        System.out.print("Ingrese el código del empleado a buscar: ");
        int codigo = lea.nextInt();

        try {
            if (manager.EmpleadosActivos(codigo)) {
                System.out.println("El empleado con código " + codigo + " está activo.");
            } else {
                System.out.println("El empleado no está activo o no existe.");
            }
        } catch (IOException e) {
            System.out.println("Error al buscar empleado: " + e.getMessage());
        }
    }
}
