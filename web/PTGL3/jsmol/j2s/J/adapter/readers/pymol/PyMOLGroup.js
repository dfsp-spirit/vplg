Clazz.declarePackage ("J.adapter.readers.pymol");
Clazz.load (["J.util.JmolList"], "J.adapter.readers.pymol.PyMOLGroup", null, function () {
c$ = Clazz.decorateAsClass (function () {
this.name = null;
this.branchNameID = null;
this.list = null;
this.branch = null;
this.visible = true;
this.occluded = false;
this.bsAtoms = null;
this.type = 0;
this.parent = null;
Clazz.instantialize (this, arguments);
}, J.adapter.readers.pymol, "PyMOLGroup");
Clazz.prepareFields (c$, function () {
this.list =  new J.util.JmolList ();
});
Clazz.makeConstructor (c$, 
function (name) {
this.name = name;
}, "~S");
$_M(c$, "addList", 
function (child) {
this.list.addLast (child);
child.parent = this;
}, "J.adapter.readers.pymol.PyMOLGroup");
$_M(c$, "set", 
function () {
if (this.parent != null) return;
});
});
