package org.firstinspires.ftc.teamcode.SubSistems;

import static com.pedropathing.math.MathFunctions.clamp;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import static java.lang.Math.sqrt;

import com.pedropathing.geometry.Pose;

public class Formula{


    public DcMotorEx intake = null;
    public DcMotorEx turela = null;
    public DcMotor lift = null;
    public Limelight3A limelight = null;

    public DcMotor lfMotor = null;
    public DcMotor lbMotor = null;
    public DcMotor rfMotor = null;
    public DcMotor rbMotor = null;

    public Servo ServoRotireComb = null;
    public DcMotor rotire = null;

    private HardwareMap hardwareMap = null;
    private Gamepad gamepad1 = null;

    public void init(HardwareMap hardwareMap, Gamepad gamepad1){

        this.hardwareMap = hardwareMap;
        this.gamepad1 = gamepad1;

        intake = hardwareMap.get(DcMotorEx.class, "intake");
        //turela = hardwareMap.get(DcMotorEx.class, "turela");
        //lift = hardwareMap.get(DcMotor.class, "lift");
       // limelight = hardwareMap.get(Limelight3A.class, "limelight");

        lfMotor = hardwareMap.get(DcMotor.class, "lf");
        lbMotor = hardwareMap.get(DcMotor.class, "lb");
        rfMotor = hardwareMap.get(DcMotor.class, "rf");
        rbMotor = hardwareMap.get(DcMotor.class, "rb");

       // ServoRotireComb = hardwareMap.get(Servo.class, "servocomb");

        lfMotor.setDirection(DcMotor.Direction.REVERSE);
        lbMotor.setDirection(DcMotor.Direction.REVERSE);
        rfMotor.setDirection(DcMotor.Direction.FORWARD);
        rbMotor.setDirection(DcMotor.Direction.FORWARD);

       // limelight.pipelineSwitch(0);

        lfMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lbMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rfMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rbMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void start(){

    }

    public void loop(){

    }

    public double get_Max(double p1,double p2, double p3, double p4){
        double max=0;
        max = Math.max(Math.abs(p1), Math.abs(p2));
        max = Math.max(max, Math.abs(p3));
        max = Math.max(max, Math.abs(p4));
        return max;
    }

    public double get_DistancePinpoint(Pose pose,int x1,int y1){
        double dist;
        double x2=pose.getY(),y2=pose.getX();
        dist=sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
        return dist;
    }


    public double Camera_Distance(double ta){
        double scale = 30665.95;
        double distance = Math.pow((scale / ta),0.5);
        return distance;

    }

    public void driveJoystick(boolean InvertControl) {

        double franademana = 1; //nefolosit, poate in viitor
        int semn = 1;

        if(InvertControl)
            semn = -1;
        else semn = 1;

        double axial   = -gamepad1.left_stick_y*semn;
        double lateral =  gamepad1.left_stick_x*semn;
        double yaw     =  gamepad1.right_stick_x;

        double lfPow = axial + lateral + yaw;
        double rfPow = axial - lateral - yaw;
        double lbPow = axial - lateral + yaw;
        double rbPow = axial + lateral - yaw;

        double max = get_Max(lfPow,lbPow,rfPow,rbPow);

        if (max > 1.0) {
            lfPow /= max;
            rfPow /= max;
            lbPow /= max;
            rbPow /= max;
        }

        lfMotor.setPower(lfPow * franademana);
        rfMotor.setPower(rfPow * franademana);
        lbMotor.setPower(lbPow * franademana);
        rbMotor.setPower(rbPow * franademana);
    }




}
