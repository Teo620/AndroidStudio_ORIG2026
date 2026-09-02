package org.firstinspires.ftc.teamcode.TeleOp;

import static androidx.core.math.MathUtils.clamp;
import static org.firstinspires.ftc.teamcode.pedroPathing.Tuning.follower;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
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




    private IMU imu;
    private PIDFController pidf;
    public DcMotor LFMotor = null, LBMotor = null, RFMotor=null, RBMotor=null;
    public DcMotor Intake = null, RGlis = null, LGlis = null;
    public Servo ServoRotireComb = null;
    private Limelight3A limelight;
    int TagID, Target_Pos = 0, GSpeed = 15;
    public static double p = 0.008, i = 0, d = 0.0002 , f = 0.12;
    boolean InvertControl = false;
    @Override
    public void init(){




        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        imu = hardwareMap.get(IMU.class, "imu");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));
        limelight.start();
        limelight.pipelineSwitch(0);

    }



    public void start()
    {

    }

    public void loop(){

        pidf.setPIDF(p, i, d, f);

        if(gamepad1.squareWasPressed())
            InvertControl=!InvertControl;

        formula.driveJoystick(InvertControl);

        if(gamepad1.right_trigger > 0)
            Intake.setPower(0.5);
        else if(gamepad1.left_trigger > 0)
            Intake.setPower(-0.5);
        else Intake.setPower(0);


        Target_Pos += -gamepad1.right_stick_y * GSpeed;
        Target_Pos = clamp(Target_Pos,0,1000);
        int Current_Pos = LGlis.getCurrentPosition();

        double Power = pidf.calculate(Current_Pos, Target_Pos);


    }


}
