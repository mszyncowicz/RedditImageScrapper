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
	private Image painting;
	private ImageView paint;
	private String url;
	

	ImageNode(StackPane viewerWrapper, String url, final double widthSetting, final double heightSetting, Stage stage){
		super(viewerWrapper,stage);
		this.url = url;
		this.widthSetting = widthSetting;
		this.heightSetting = heightSetting;
	}
	@Override
	public void setUp() throws Exception {
		paint = new ImageView();
		paint.setPreserveRatio(true);
		paint.setFitWidth(widthSetting);
		paint.maxHeight(heightSetting);
		paint.maxWidth(widthSetting);
		setPhoto();
		
	}

	@Override
	public void dispose() {
		this.viewerWrapper.getChildren().remove(paint);
		paint = null;
		this.painting.cancel();
		painting= null;

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
		setImage(new Image(url));
		
	}
	private void setPhoto(){
		while (url.charAt(url.length() - 1) != 'g' && url.charAt(url.length() - 1) != 'f') {
			url = url.substring(0, url.length() - 1);
		}
		painting = new Image(url, true);
		paint.setImage(painting);
		viewerWrapper.getChildren().add(paint);
		viewerWrapper.setMaxHeight(paint.getFitHeight());
	}
	
	private void zoom() {
		if (isZoomed) {
			paint.setFitWidth(widthSetting);
			isZoomed = false;
		} else {
			double newWidth = painting.getWidth();
			double newHeight = painting.getHeight();
			if (newWidth > paint.maxWidth(this.currentStage.getMaxWidth()- 500)) {
				paint.setFitWidth(this.currentStage.getMaxWidth()- 500);
			} else if (newHeight > paint.maxHeight(this.currentStage.getMaxHeight() - 200)) {
				paint.setFitHeight(this.currentStage.getMaxHeight() - 200);
			} else {
				paint.setFitWidth(painting.getWidth());
			}

			isZoomed = true;
		}

	}
	private void setImage(Image a) {
		Timeline timeline1 = new Timeline();
		Timeline timeline2 = new Timeline();
		timeline1.getKeyFrames().addAll(
				new KeyFrame(Duration.ZERO, new KeyValue(
						paint.opacityProperty(), 1)

				),
				new KeyFrame(Duration.millis(300), new KeyValue(paint
						.opacityProperty(), 0)));
		timeline2.getKeyFrames().addAll(
				new KeyFrame(Duration.ZERO, new KeyValue(
						paint.opacityProperty(), 0)

				),
				new KeyFrame(Duration.millis(200), new KeyValue(paint
						.opacityProperty(), 1)));
		timeline1.setOnFinished(e -> {
			paint.setImage(a);
			paint.setFitWidth(widthSetting);
			isZoomed = false;
			timeline2.play();
		});
		timeline1.play();

	}
}
