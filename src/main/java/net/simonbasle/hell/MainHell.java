package net.simonbasle.hell;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import javax.swing.JButton;

import net.simonbasle.hell.service.CommentService;
import net.simonbasle.hell.service.DocumentService;
import net.simonbasle.hell.service.MetaService;
import net.simonbasle.hell.service.PictureService;
import net.simonbasle.hell.service.UserService;
import net.simonbasle.model.Comment;
import net.simonbasle.model.Document;
import net.simonbasle.model.JsonArray;
import net.simonbasle.model.JsonObject;
import net.simonbasle.model.Meta;
import net.simonbasle.model.PicMeta;
import net.simonbasle.model.User;

public class MainHell {

	public static void main(String[] args) {
		Callback<List<String>> somethingToDo = new Callback<List<String>>() {

			@Override
			public void onSuccess(List<String> result) {
				// TODO Auto-generated method stub

			}
		};

DocumentService.find("userId", new Callback<List<Document>>() {
public void onSuccess(List<Document> result) {
	final List<String> jsonList = new ArrayList<String>(10);
	int taken = 0;
	for (Document doc : result) {
		if (taken >= 10)
			break;
		if (!doc.isStarred())
			continue;
		taken++;
		final CountDownLatch rendezVous = new CountDownLatch(3);
		final JsonObject jsonBuffer = new JsonObject();
		jsonBuffer.appendInt("id", doc.getId());
		jsonBuffer.append("text", doc.getText());
		CommentService.findForDoc(doc, new Callback<List<Comment>>() {
			public void onSuccess(List<Comment> comments) {
				final JsonArray commentArray = new JsonArray();
				CountDownLatch userLatch = new CountDownLatch(comments.size());
				for (Comment c : comments) {
					JsonObject cj = new JsonObject();
					cj.append("content", c.getText());
					cj.append("date", c.getDate());
					UserService.find(c.getUserId(), new Callback<User>() {
						public void onSuccess(User user) {
							cj.append("author", user.getName());
							cj.append("nickname", user.getLogin());
							cj.append("email", user.getEmail());

							commentArray.addElement(cj);
							userLatch.countDown();
						}
					});
				}
				userLatch.await();
				jsonBuffer.addArray("comments", commentArray);
				rendezVous.countDown();
			}
		});
		MetaService.findForDoc(doc, new Callback<List<Meta>>() {
			public void onSuccess(List<Meta> metas) {
				jsonBuffer.addArray("meta", jsonifyMetaList(metas));
				rendezVous.countDown();
			}
		});
		PictureService.findAllMetas(doc.getPictures(), new Callback<List<PicMeta>>() {
			public void onSuccess(List<PicMeta> picMetas) {
				jsonBuffer.addArray("pictures", jsonifyPicList(picMetas));
				rendezVous.countDown();
			}
		});
		rendezVous.await();
		jsonList.add(jsonBuffer.toString());
	}
	somethingToDo.onSuccess(jsonList);
}
});
	}

	private static JsonObject jsonify(Object o) {
		return new JsonObject();
	}
	
	private static JsonArray jsonifyMetaList(List<Meta> metas) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private static JsonArray jsonifyPicList(List<PicMeta> picMetas) {
		// TODO Auto-generated method stub
		return null;
	}

}
