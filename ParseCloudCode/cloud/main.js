
// Use Parse.Cloud.define to define as many cloud functions as you want.


/// Add user to 'defaultUsers' role when it has been created
Parse.Cloud.afterSave(Parse.User, function(request) {

	Parse.Cloud.useMasterKey();
	// Everything after this point will bypass ACLs and other security
	// even if I do things besides just updating a Post object.
	query = new Parse.Query("_Role");
	query.get("pRRqGmwMVV", {
	
		success: function(object) {
		    // object is an instance of Parse.Object.
			object.relation("users").add(request.user);
			object.save(null, {
				success: function(obj) {
					// The save was successful.
				},
				error: function(obj, error) {
					// The save failed.  Error is an instance of Parse.Error.
					console.error("save.then error " + error.code + " : " + error.message);
				}
			});
  		},
		error: function(object, error) {
			// error is an instance of Parse.Error.
			console.error("query.get error " + error.code + " : " + error.message);
  		}
	});
});
