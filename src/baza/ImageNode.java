package baza;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ImageNode extends MediaNode {
	private Image image;
	private ImageView imageView;
	private String url;
	

	ImageNode(StackPane viewerWrapper, String url, final double widthSetting, final double heightSetting, Stage stage){
		super(viewerWrapper,stage);
		this.url = url;
		this.widthSetting = widthSetting;
		this.heightSetting = heightSetting;
	}
	@Override
	public void setUp() throws Exception {
		imageView = new ImageView();
		imageView.setPreserveRatio(true);
		imageView.setFitWidth(widthSetting);
		imageView.maxHeight(heightSetting);
		imageView.maxWidth(widthSetting);
		setPhoto();
		onClick();
		
	}

	@Override
	public void dispose() {
		this.viewerWrapper.getChildren().remove(imageView);
		this.image.cancel();
		//this.imageView = null;
		this.image = null;
	}

	@Override
	public void onClick() {
		this.viewerWrapper.setOnMouseClicked(e -> {
			zoom();
			e.consume();
		});
		

	}
	@Override
	public void setNext(String url) throws Exception {
		dispose();
		setImage(new Image(url));
		
	}
	private void setPhoto(){
		while (url.charAt(url.length() - 1) != 'g' && url.charAt(url.length() - 1) != 'f') {
			url = url.substring(0, url.length() - 1);
		}
		image = new Image(url, true);
		imageView.setImage(image);
		viewerWrapper.getChildren().add(imageView);
		viewerWrapper.setMaxHeight(imageView.getFitHeight());
	}
	
	private void zoom() {
		if (isZoomed) {
			imageView.setFitWidth(widthSetting);
			isZoomed = false;
		} else {
			double newWidth = image.getWidth();
			double newHeight = image.getHeight();
			if (newWidth > imageView.maxWidth(this.currentStage.getMaxWidth()- 500)) {
				imageView.setFitWidth(image.getWidth()-500);
			//	imageView.setFitWidth(this.currentStage.getMaxWidth()- 500);
			} else if (newHeight > imageView.maxHeight(this.currentStage.getMaxHeight() - 200)) {
				imageView.setFitHeight(image.getHeight()-200);
			//	imageView.setFitHeight(this.currentStage.getMaxHeight() - 200);
			} else {
				imageView.setFitWidth(image.getWidth());
			}

			isZoomed = true;
		}

	}
	private void setImage(Image a) {
		this.image =a;
		Timeline timeline1 = new Timeline();
		Timeline timeline2 = new Timeline();
		timeline1.getKeyFrames().addAll(
				new KeyFrame(Duration.ZERO, new KeyValue(
						imageView.opacityProperty(), 1)

				),
				new KeyFrame(Duration.millis(300), new KeyValue(imageView
						.opacityProperty(), 0)));
		timeline2.getKeyFrames().addAll(
				new KeyFrame(Duration.ZERO, new KeyValue(
						imageView.opacityProperty(), 0)

				),
				new KeyFrame(Duration.millis(200), new KeyValue(imageView
						.opacityProperty(), 1)));
		timeline1.setOnFinished(e -> {
			imageView.setImage(a);
			imageView.setFitWidth(widthSetting);
			isZoomed = false;
			timeline2.play();
		});
		timeline1.play();

	}
}
