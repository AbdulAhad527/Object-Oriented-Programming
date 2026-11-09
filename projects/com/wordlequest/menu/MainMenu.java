/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.wordlequest.menu;

/**
 *
 * @author ML
 */
import java.util.Scanner;
public class MainMenu {
 @SuppressWarnings("resource")
public int showMenu(){    
Scanner input=new Scanner(System.in);
System.out.println("-------- Wordle Quest ----------");
System.out.println("1.Play");
System.out.println("2.Exit");
int playOrExit=1;
try
{
    playOrExit=input.nextInt();
}
catch (Exception e)
{
System.out.println("Ivalid Entry");
}
return playOrExit;
}
}
