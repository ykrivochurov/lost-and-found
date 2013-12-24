users = db.system.users.find({
    user : 'admin'
});

if (users.count() == 0)
    db.addUser('admin', 'pAssw0rd');