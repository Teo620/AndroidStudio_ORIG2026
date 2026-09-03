package org.firstinspires.ftc.teamcode.TeleOp;

import static androidx.core.math.MathUtils.clamp;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
//import com.arcrobotics.ftclib.controller.PIDFController;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.SubSistems.Formula;

@Config
@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="test nebun", group="Linear OpMode")
public class Test_TeleOp extends OpMode {
   //private Formula formula = new Formula(); //Cele mai bune formule

    public FtcDashboard dashboard;




    private IMU imu;
   // private PIDFController pidf;
    public DcMotor LFMotor = null, LBMotor = null, RFMotor=null, RBMotor=null;
    public DcMotor Intake = null, RGlis = null, LGlis = null;
    public Servo ServoRotireComb = null, Clamp = null, Servo_Polen = null, Clamp_Angle = null;
    private Limelight3A limelight;
    int TagID, Target_Pos = 0, GSpeed = 5;
    public static double p = 0.008, i = 0, d = 0.0002 , f = 0.12;
    boolean InvertControl = false;
    @Override
    public void init(){


        LFMotor = hardwareMap.get(DcMotor.class, "lf");
        LBMotor = hardwareMap.get(DcMotor.class, "lb");
        RFMotor = hardwareMap.get(DcMotor.class, "rf");
        RBMotor = hardwareMap.get(DcMotor.class, "rb");
        LFMotor.setDirection(DcMotor.Direction.REVERSE);
        LBMotor.setDirection(DcMotor.Direction.REVERSE);
        /*
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));
        limelight.start();
        limelight.pipelineSwitch(0);
*/

        Intake = hardwareMap.get(DcMotor.class ,"intake");
        Clamp = hardwareMap.get(Servo.class ,"clamp");
        LGlis= hardwareMap.get(DcMotor.class, "lg");
        RGlis = hardwareMap.get(DcMotor.class, "rg");
        LGlis.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RGlis.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LGlis.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        RGlis.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        RGlis.setDirection(DcMotorSimple.Direction.REVERSE);
        Servo_Polen = hardwareMap.get(Servo.class ,"polen");
        Clamp_Angle = hardwareMap.get(Servo.class ,"angle");
    }



    public void start()
    {

    }

    public void loop(){

       // pidf.setPIDF(p, i, d, f);

        if(gamepad1.squareWasPressed())
            InvertControl=!InvertControl;

        //formula.driveJoystick(InvertControl);



        int semn = 1;

        if(InvertControl)
            semn = -1;
        else semn = 1;

        double axial   = -gamepad1.left_stick_y*semn;
        double lateral =  gamepad1.left_stick_x*semn;
        double yaw     =  gamepad1.right_stick_x*semn;

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

        LFMotor.setPower(lfPow);
        RFMotor.setPower(rfPow);
        LBMotor.setPower(lbPow);
        RBMotor.setPower(rbPow);

        if(gamepad1.right_trigger > 0)
            Intake.setPower(0.61);
        else if(gamepad1.left_trigger > 0)
            Intake.setPower(-0.61);
        else Intake.setPower(0);

        if(gamepad1.cross)
            Clamp.setPosition(0.50); //DESCHIS
        else if(gamepad1.triangle)
            Clamp.setPosition(0.3); //INCHIS


        if(gamepad1.dpad_up){
            RGlis.setPower(0.5);      //SUS
            LGlis.setPower(0.5);
        }
        else if(gamepad1.dpad_down){
            RGlis.setPower(-0.5);      //JOS
            LGlis.setPower(-0.5);
        }
        else{
            RGlis.setPower(0);      //Stai sefule
            LGlis.setPower(0);
        }


        if(gamepad1.dpad_left)
            Servo_Polen.setPosition(0);
        else if(gamepad1.dpad_right)
            Servo_Polen.setPosition(0.30);

        if(gamepad1.left_bumper)
            Clamp_Angle.setPosition(0.4);
        else if(gamepad1.right_bumper)
            Clamp_Angle.setPosition(0.75);




        if(gamepad2.dpad_up)
            RBMotor.setPower(0.5);

        if(gamepad2.dpad_down)
            RFMotor.setPower(0.5);
        if(gamepad2.dpad_left)
            LFMotor.setPower(0.5);
        if(gamepad2.dpad_right)
            LBMotor.setPower(0.5);



        if(gamepad1.left_bumper)
            Clamp_Angle.setPosition(0.4);
        else if(gamepad1.right_bumper)
            Clamp_Angle.setPosition(0.75);

        /*
        Target_Pos += -gamepad1.right_stick_y * GSpeed;
        Target_Pos = clamp(Target_Pos,0,1000);
        int Current_Pos = LGlis.getCurrentPosition();

        double Power = pidf.calculate(Current_Pos, Target_Pos);

        RGlis.setPower(Power);
        LGlis.setPower(Power);
*/
























        /*

        ampu ii la mare
        bagpu ii la munte


         */
    }
    public double get_Max(double p1,double p2, double p3, double p4){
        double max=0;
        max = Math.max(Math.abs(p1), Math.abs(p2));
        max = Math.max(max, Math.abs(p3));
        max = Math.max(max, Math.abs(p4));
        return max;
    }


}
