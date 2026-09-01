package org.firstinspires.ftc.teamcode.TeleOp;

import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.follower;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import com.qualcomm.robotcore.hardware.CRServo;


@Config
@TeleOp(name="TeleOpAlbastru", group="Linear OpMode")
public class TeleOpAlbastru extends OpMode {

    //private TurretMechanismTutorial turret = new TurretMechanismTutorial(); //Sistemul Stanga-Dreapta
   // private Formula formula = new Formula(); //Cele mai bune formule

    public FtcDashboard dashboard;


    private IMU imu;
    public DcMotorEx TURELA = null;

    public DcMotor LIFT = null;

    public DcMotor LFMotor = null, LBMotor = null, RFMotor=null, RBMotor=null;

    @Override
    public void init(){

    }

    public void start(){}

    public void loop(){

       //va c=face cei ce stiu
    }


}
