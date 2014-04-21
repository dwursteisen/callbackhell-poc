package net.simonbasle.heaven;

import net.simonbasle.heaven.service.CommentService;
import net.simonbasle.heaven.service.DocumentService;
import net.simonbasle.heaven.service.MetaService;
import net.simonbasle.heaven.service.PictureService;
import net.simonbasle.heaven.service.UserService;
import net.simonbasle.model.JsonArray;
import net.simonbasle.model.JsonObject;
import net.simonbasle.model.User;
import rx.Observable;
	public class MainHeaven {

		public static void main(String[] args) {
Observable<JsonObject> fullDocumentJson = DocumentService.find("user")
.filter(doc -> doc.isStarred())
.take(10)
.map(doc -> {
	Observable<JsonObject> oc = CommentService.findForDoc(doc)
	.map(c -> {	User u = UserService.find(c.getUserId()).toBlockingObservable().first();
				JsonObject result = jsonify(c).append("author", u.getName());
				return result.append("nickname", u.getLogin()).append("email", u.getEmail());});

	Observable<JsonObject> om = MetaService.findForDoc(doc).map(m -> jsonify(m));
	Observable<JsonObject> op = PictureService.findAllMetas(doc.getPictures()).map(p -> jsonify(p));
			
	JsonObject docJson = new JsonObject().appendInt("id", doc.getId()).append("text", doc.getText());
			
	JsonArray c = new JsonArray();
	docJson.addArray("comments", c);
	oc.reduce(c, (array, elem) -> array.addElement(elem));

	JsonArray m = new JsonArray();
	docJson.addArray("meta", m);
	om.reduce(m, (array, elem) -> array.addElement(elem));

	JsonArray p = new JsonArray();
	docJson.addArray("pictures", p);
	op.reduce(p, (array, elem) -> array.addElement(elem));
			
	return docJson;
});
		}
		
		private static JsonObject jsonify(Object o) {
			return new JsonObject();
		}
	}

