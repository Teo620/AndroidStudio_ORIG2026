package org.firstinspires.ftc.teamcode.Auto;

import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.follower.Follower;
import com.pedropathing.geometry.BezierCurve;
import com.pedropathing.geometry.BezierLine;
import com.pedropathing.geometry.Pose;
import com.pedropathing.paths.PathChain;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.pedropathing.util.Timer;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.pedropathing.geometry.Pose;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import org.firstinspires.ftc.teamcode.pedroPathing.Constants;
import org.firstinspires.ftc.teamcode.SubSistems.Formula;

import java.util.List;

@Config
@Autonomous(name="Auto_Albastru", group="Linear OpMode")
public class Auto_Albastru extends OpMode {

    private Follower follower;
    private Formula formula = new Formula();

    private Timer pathTimer, opModeTimer,aux,shootTimer,timp_pentru_tras_minge,auxTime,timp_pentru_aruncat_minge;

    public DcMotorEx TURELA = null;
    public DcMotor LIFT = null;
    public DcMotor INTAKE=null;
    private final Pose startPose = new Pose(20,123,Math.toRadians(140));
    private final Pose shootPose = new Pose(59,(84),Math.toRadians(180));
    private final Pose GateTake=new Pose(10,(65),Math.toRadians(150));
    private final Pose[] Parcari ={
            new Pose(10,(65),Math.toRadians(150)),
            new Pose(10,(65),Math.toRadians(150)),
            new Pose(10,(65),Math.toRadians(150))};
    private Pose Parcare_Finala=new Pose(0,(0),Math.toRadians(0));
    Servo UNGHITURELA=null;
    Limelight3A limelight;
    IMU imu;
    int lastdistance=0;
    double pozitieservo;
    int TagID;
    int facut=0;
    private PathChain driveStartPosShootPos;

    public enum PathState{
        startpos_shootpos, shoot, gate_take_come_back;
    }

    PathState pathState;
    Pose lastPose;

    public void buildPaths(){
        driveStartPosShootPos= follower.pathBuilder()
                .addPath(new BezierLine(startPose,shootPose))
                .setLinearHeadingInterpolation(startPose.getHeading(),shootPose.getHeading())
                .build();

    }

    public PathChain Drive(Pose a,Pose b){
        return follower.pathBuilder()
                .addPath(new BezierLine(a,b))
                .setLinearHeadingInterpolation(a.getHeading(), b.getHeading())
                .build();
    }


    public void startPathUpdate(){
        switch (pathState) {

            case gate_take_come_back:
                if(facut==0){
                    follower.followPath(Drive(GateTake,shootPose), true);
                    facut=1;
                }
                if(!follower.isBusy()) {
                    setPathState(PathState.shoot);
                }
                break;

            default:    break;
        }
    }
    public void setPathState(PathState newState){

        pathState=newState;
        pathTimer.resetTimer();
        facut=0;
    }
    @Override
    public void init(){

        INTAKE =hardwareMap.get(DcMotor.class,"intake");
        //TURELA =hardwareMap.get(DcMotorEx.class,"turela");
        LIFT =hardwareMap.get(DcMotor.class,"lift");
        imu = hardwareMap.get(IMU.class, "imu");
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        RevHubOrientationOnRobot revHubOrientationOnRobot = new RevHubOrientationOnRobot(RevHubOrientationOnRobot.LogoFacingDirection.LEFT,
                RevHubOrientationOnRobot.UsbFacingDirection.UP);
        imu.initialize(new IMU.Parameters(revHubOrientationOnRobot));
        limelight.pipelineSwitch(0);
        limelight.start();

        pathState=PathState.startpos_shootpos;
        pathTimer=new Timer();
        opModeTimer=new Timer();
        timp_pentru_tras_minge=new Timer();
        auxTime=new Timer();
        follower= Constants.createFollower(hardwareMap);
        buildPaths();
        follower.setPose(startPose);

    }

    public void init_loop()
    {
        limelight.updateRobotOrientation(Math.toDegrees(follower.getPose().getHeading()));
        LLResult result = limelight.getLatestResult();

        if (result != null && result.isValid()) {
            List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();

            if (!fiducials.isEmpty()) {
                // Lock in the ID to your variable
                TagID = fiducials.get(0).getFiducialId();
            }
        }
    }

    public void start(){
        opModeTimer.resetTimer();
        setPathState(pathState);

        int Case = 0;

        if(TagID==21){
            Case=0;
        }else if(TagID==22){
            Case=1;
        }else{
            Case=2;
        }

        Parcare_Finala=Parcari[Case];
    }

    @Override
    public void loop(){
        follower.update();
        startPathUpdate();
        telemetry.addData("ID OBELISK",TagID);
        telemetry.addData("FACEM LOOP",1);
        telemetry.update();


    }


}

















