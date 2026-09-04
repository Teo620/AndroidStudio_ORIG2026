package org.firstinspires.ftc.teamcode.TeleOp;

import static androidx.core.math.MathUtils.clamp;
import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.follower;

import static java.lang.Math.abs;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.arcrobotics.ftclib.controller.PIDFController;

import org.firstinspires.ftc.teamcode.SubSistems.Formula;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;

@Config
@com.qualcomm.robotcore.eventloop.opmode.TeleOp(name="TeleOp", group="Linear OpMode")
public class TeleOp extends OpMode {
    private Formula formula = new Formula(); //Cele mai bune formule

    public FtcDashboard dashboard;

    public double start_polen = 0, start_angle = 0.7, start_clamp = 0.5;
    private PIDFController pidf;

    private IMU imu;
    public DcMotor LFMotor = null, LBMotor = null, RFMotor = null, RBMotor = null;
    public DcMotor Intake = null, RGlis = null, LGlis = null;

    public Servo Clamp = null, Servo_Polen = null, Clamp_Angle = null;
    public Servo ServoRotireComb = null;
    private Limelight3A limelight;
    int TagID,ok=0,ok_clema=0,ok_polen=0;
    float Target_Pos = 0, GSpeed = 1;

    public static double p = 0.005, i = 0, d = 0.0003, f = 0.05;

    boolean InvertControl = false;

    @Override
    public void init() {

        formula.init(hardwareMap, gamepad1);
        LFMotor = hardwareMap.get(DcMotor.class, "lf");
        LBMotor = hardwareMap.get(DcMotor.class, "lb");
        RFMotor = hardwareMap.get(DcMotor.class, "rf");
        RBMotor = hardwareMap.get(DcMotor.class, "rb");
        LFMotor.setDirection(DcMotor.Direction.REVERSE);
        LBMotor.setDirection(DcMotor.Direction.REVERSE);

        pidf = new PIDFController(p, i, d, f);
        Intake = hardwareMap.get(DcMotor.class, "intake");
        Clamp = hardwareMap.get(Servo.class, "clamp");
        Servo_Polen = hardwareMap.get(Servo.class, "polen");
        Clamp_Angle = hardwareMap.get(Servo.class, "angle");
        LGlis = hardwareMap.get(DcMotor.class, "lg");
        RGlis = hardwareMap.get(DcMotor.class, "rg");
        LGlis.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        RGlis.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        LGlis.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        RGlis.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        RGlis.setDirection(DcMotor.Direction.REVERSE);


        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));
        limelight.start();
        limelight.pipelineSwitch(0);

        Clamp.setPosition(start_clamp);
        Clamp_Angle.setPosition(start_angle);
        Servo_Polen.setPosition(start_polen);

    }


    public void start() {

    }

    public void loop() {

        pidf.setPIDF(p, i, d, f);

        if (gamepad1.squareWasPressed())
            InvertControl = !InvertControl;

        formula.driveJoystick(InvertControl);

        if (gamepad1.right_trigger > 0)
            Intake.setPower(1);
        else if (gamepad1.left_trigger > 0)
            Intake.setPower(-1);
        else Intake.setPower(0);

        if (gamepad1.crossWasPressed())
        {
            if(ok_clema==0){
                Clamp.setPosition(0.7); //DESCHIS
                ok_clema=1;
            }else{
                Clamp.setPosition(0.3); //INCHIS
                ok_clema=0;
            }

        }

        if (gamepad1.triangleWasPressed()){
            if(ok_polen==0){
                Servo_Polen.setPosition(0); //INCHIS
                ok_polen=1;
            }else{
                Servo_Polen.setPosition(0.45); //DESCHIS
                ok_polen=0;
            }
        }
        if (gamepad1.dpad_right){
            Clamp_Angle.setPosition(0.4);
        }

        if (gamepad1.right_bumper)
            Clamp_Angle.setPosition(0.6); //SUS
        else if (gamepad1.left_bumper)
            Clamp_Angle.setPosition(0.75);  //JOS

        if(gamepad1.circleWasPressed())
        {
            if(ok==0)
            {
                ok=1;
                Clamp_Angle.setPosition(0.6);  //Deschis tati
                Clamp.setPosition(0.7);
                Servo_Polen.setPosition(0.45);
                Target_Pos=50;
            }
            else{
                Clamp_Angle.setPosition(0.75);  //Inchis tati
                Clamp.setPosition(0.4);
                Servo_Polen.setPosition(0);
                Target_Pos=0;
                ok=0;
            }

        }


        //  LOGICA GLISIERE

        if (gamepad1.dpad_up)
            Target_Pos += GSpeed;

        if (gamepad1.dpad_down)
            Target_Pos -= GSpeed;

        Target_Pos = clamp(Target_Pos, 0, 160);
        int Current_Pos = LGlis.getCurrentPosition();
        double power = pidf.calculate(Current_Pos, Target_Pos);
        power = clamp(power, -0.4, 0.4);


        LGlis.setPower(power);
        RGlis.setPower(power);


        telemetry.addData("Case Pos", RGlis.getCurrentPosition());
        telemetry.addData("RGlis Pos", RGlis.getCurrentPosition());
        telemetry.addData("LGlis Pos", LGlis.getCurrentPosition());
        telemetry.addData("Target", Target_Pos);
        telemetry.addData("Power", power);
        telemetry.update();
    }
}
