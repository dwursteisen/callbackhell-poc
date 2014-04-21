package net.simonbasle.heaven;

import java.util.List;

import jdk.nashorn.internal.scripts.JO;

import com.sun.media.sound.JARSoundbankReader;

import net.simonbasle.heaven.service.CommentService;
import net.simonbasle.heaven.service.DocumentService;
import net.simonbasle.heaven.service.MetaService;
import net.simonbasle.heaven.service.PictureService;
import net.simonbasle.heaven.service.UserService;
import net.simonbasle.model.Comment;
import net.simonbasle.model.Document;
import net.simonbasle.model.JsonArray;
import net.simonbasle.model.JsonObject;
import net.simonbasle.model.Meta;
import net.simonbasle.model.PicMeta;
import net.simonbasle.model.User;
import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;

public class MainRx {

	public static void main(String[] args) {
Observable<JsonObject> fullDocumentJson = DocumentService.find("user")
.filter(new Func1<Document, Boolean>() {
	public Boolean call(Document doc) {
		return doc.isStarred();
	}})
.take(10)
.map(new Func1<Document, JsonObject>() {
	public JsonObject call(Document doc) {
		Observable<JsonObject> oc = CommentService.findForDoc(doc)
		.map(new Func1<Comment, JsonObject>() {
			public JsonObject call(Comment c) {
				User u = UserService.find(c.getUserId())
					.toBlockingObservable().first();
				JsonObject result = jsonify(c).append("author", u.getName());
				return result.append("nickname", u.getLogin()).append("email", u.getEmail());
			}});

		Observable<JsonObject> om = MetaService.findForDoc(doc)
		.map(new Func1<Meta, JsonObject>() {
			public JsonObject call(Meta m) {
				return jsonify(m);
			}});
	
		Observable<JsonObject> op = PictureService.findAllMetas(doc.getPictures())
		.map(new Func1<PicMeta, JsonObject>() {
			public JsonObject call(PicMeta p) {
				return jsonify(p);
			}});
			
		Func2<JsonArray, JsonObject, JsonArray> arrayAggregator = new Func2<JsonArray, JsonObject, JsonArray>() {
			public JsonArray call(JsonArray t1, JsonObject t2) {
				return t1.addElement(t2);
			}};
			
		JsonObject docJson = new JsonObject().appendInt("id", doc.getId()).append("text", doc.getText());
			
		JsonArray c = new JsonArray();
		docJson.addArray("comments", c);
		oc.reduce(c, arrayAggregator);

		JsonArray m = new JsonArray();
		docJson.addArray("meta", m);
		om.reduce(m, arrayAggregator);

		JsonArray p = new JsonArray();
		docJson.addArray("pictures", p);
		op.reduce(p, arrayAggregator);
			
		return docJson;
	}
});
	}
	
	private static JsonObject jsonify(Object o) {
		return new JsonObject();
	}
}
