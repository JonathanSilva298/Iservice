const functions = require("firebase-functions");

const admin = require("firebase-admin");

admin.initializeApp();

exports.pushes = functions.firestore.document("notifications/{token}")
  .onCreate((snap, context) => {
    const document = snap.data();
    console.log("document is", document)
  });