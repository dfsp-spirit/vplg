Clazz.declarePackage ("J.adapter.readers.pymol");
Clazz.load (null, "J.adapter.readers.pymol.JmolObject", ["java.lang.Boolean", "$.Float", "J.adapter.readers.pymol.PyMOLReader", "J.atomdata.RadiusData", "J.constant.EnumVdw", "J.modelset.Atom", "J.util.BSUtil", "$.Escape", "$.P3", "$.SB"], function () {
c$ = Clazz.decorateAsClass (function () {
this.id = 0;
this.bsAtoms = null;
this.info = null;
this.size = -1;
this.colixes = null;
this.colors = null;
this.branchNameID = null;
this.argb = 0;
this.translucency = 0;
this.visible = true;
this.rd = null;
Clazz.instantialize (this, arguments);
}, J.adapter.readers.pymol, "JmolObject");
Clazz.makeConstructor (c$, 
function (id, branchNameID, bsAtoms, info) {
this.id = id;
this.bsAtoms = bsAtoms;
this.info = info;
this.branchNameID = branchNameID;
}, "~N,~S,J.util.BS,~O");
$_M(c$, "offset", 
function (modelOffset, atomOffset) {
if (modelOffset > 0) {
switch (this.id) {
case 4115:
var i = (this.info).intValue ();
if (i >= 0) this.info = Integer.$valueOf (modelOffset + i);
return;
case 1073742032:
var movie = this.info;
var frames = movie.get ("frames");
for (var j = frames.length; --j >= 0; ) frames[j] += modelOffset;

return;
}
}if (atomOffset <= 0) return;
if (this.id == 1060866) {
var map = (this.info).values ();
for (var o, $o = map.iterator (); $o.hasNext () && ((o = $o.next ()) || true);) J.util.BSUtil.offset (o, 0, atomOffset);

return;
}if (this.bsAtoms != null) J.util.BSUtil.offset (this.bsAtoms, 0, atomOffset);
if (this.colixes != null) {
var c =  Clazz.newShortArray (this.colixes.length + atomOffset, 0);
System.arraycopy (this.colixes, 0, c, atomOffset, this.colixes.length);
this.colixes = c;
}}, "~N,~N");
$_M(c$, "finalizeObject", 
function (m, bsCarb) {
var sm = m.shapeManager;
var modelIndex = this.getModelIndex (m);
var sID;
var sb = null;
switch (this.id) {
case 1060866:
sm.viewer.defineAtomSets (this.info);
return;
case 1073742032:
sm.viewer.setMovie (this.info);
return;
case 4115:
var frame = (this.info).intValue ();
if (frame >= 0) {
sm.viewer.setCurrentModelIndex (frame);
} else {
sm.viewer.setAnimationRange (-1, -1);
sm.viewer.setCurrentModelIndex (-1);
}return;
case 1087373318:
return;
case 3145770:
sm.viewer.displayAtoms (this.bsAtoms, false, false, Boolean.TRUE, true);
return;
case 5:
sm.loadShape (this.id);
sm.setShapePropertyBs (this.id, "textLabels", this.info, this.bsAtoms);
return;
case 1:
case 0:
break;
case 10:
case 9:
J.util.BSUtil.andNot (this.bsAtoms, bsCarb);
break;
case 16:
sm.loadShape (this.id);
sm.setShapePropertyBs (this.id, "ignore", J.util.BSUtil.copyInvert (this.bsAtoms, sm.viewer.getAtomCount ()), null);
break;
case 13:
this.setPutty (sm);
break;
case 24:
if (this.bsAtoms == null ? !this.visible : modelIndex < 0) return;
break;
default:
if (!this.visible) return;
break;
}
switch (this.id) {
case 6:
if (modelIndex < 0) return;
sm.loadShape (this.id);
var md = this.info;
md.setModelSet (m);
var points = md.points;
for (var i = points.size (); --i >= 0; ) (points.get (i)).modelIndex = modelIndex;

sm.setShapePropertyBs (this.id, "measure", md, this.bsAtoms);
return;
case 23:
var cgo = this.info;
sID = cgo.get (cgo.size () - 1);
sm.viewer.setCGO (cgo);
break;
case 1073742016:
var mep = this.info;
sID = mep.get (mep.size () - 2).toString ();
var mapID = mep.get (mep.size () - 1).toString ();
var min = J.adapter.readers.pymol.PyMOLReader.floatAt (J.adapter.readers.pymol.PyMOLReader.listAt (mep, 3), 0);
var max = J.adapter.readers.pymol.PyMOLReader.floatAt (J.adapter.readers.pymol.PyMOLReader.listAt (mep, 3), 2);
sb =  new J.util.SB ();
sb.append ("set isosurfacekey true;isosurface ID ").append (J.util.Escape.eS (sID)).append (" map \"\" ").append (J.util.Escape.eS (mapID)).append (";color isosurface range " + min + " " + max + ";isosurface colorscheme rwb");
break;
case 1073742018:
modelIndex = sm.viewer.getCurrentModelIndex ();
var mesh = this.info;
sID = mesh.get (mesh.size () - 2).toString ();
sb =  new J.util.SB ();
sb.append ("isosurface ID ").append (J.util.Escape.eS (sID)).append (" model ").append (m.models[modelIndex].getModelNumberDotted ()).append (" color ").append (J.util.Escape.escapeColor (this.argb)).append (" \"\" ").append (J.util.Escape.eS (sID)).append (" mesh nofill frontonly");
var within = J.adapter.readers.pymol.PyMOLReader.floatAt (J.adapter.readers.pymol.PyMOLReader.listAt (J.adapter.readers.pymol.PyMOLReader.listAt (mesh, 2), 0), 11);
var list = J.adapter.readers.pymol.PyMOLReader.listAt (J.adapter.readers.pymol.PyMOLReader.listAt (J.adapter.readers.pymol.PyMOLReader.listAt (mesh, 2), 0), 12);
if (within > 0) {
var pt =  new J.util.P3 ();
sb.append (";isosurface slab within ").appendF (within).append (" [ ");
for (var j = list.size () - 3; j >= 0; j -= 3) {
J.adapter.readers.pymol.PyMOLReader.pointAt (list, j, pt);
sb.append (J.util.Escape.eP (pt));
}
sb.append (" ]");
}sb.append (";set meshScale ").appendI (Clazz.doubleToInt (this.size / 500));
break;
case 24:
sID = (this.bsAtoms == null ? this.info : this.branchNameID);
sb =  new J.util.SB ();
sb.append ("isosurface ID ").append (J.util.Escape.eS (sID));
if (this.bsAtoms == null) {
modelIndex = sm.viewer.getCurrentModelIndex ();
sb.append (" model ").append (m.models[modelIndex].getModelNumberDotted ()).append (" color density sigma 1.0").append (" \"\" ").append (J.util.Escape.eS (sID));
break;
}if (this.argb == 0) sm.setShapePropertyBs (0, "colors", this.colors, this.bsAtoms);
var lighting = this.info;
var resolution = "";
if (lighting == null) {
lighting = "mesh nofill";
resolution = " resolution 1.5";
}sb.append (" model ").append (m.models[modelIndex].getModelNumberDotted ()).append (resolution).append (" select ").append (J.util.Escape.eBS (this.bsAtoms)).append (" only").append (" solvent ").appendF (this.size / 1000);
if (this.argb == 0) sb.append (" map property color");
 else sb.append (" color ").append (J.util.Escape.escapeColor (this.argb));
sb.append (" frontOnly ").append (lighting);
if (this.translucency > 0) sb.append (" translucent ").appendF (this.translucency);
break;
}
if (sb != null) {
sm.viewer.runScript (sb.toString ());
return;
}if (this.size != -1 || this.rd != null) sm.setShapeSizeBs (this.id, this.size, this.rd, this.bsAtoms);
if (this.argb != 0) sm.setShapePropertyBs (this.id, "color", Integer.$valueOf (this.argb), this.bsAtoms);
if (this.translucency > 0) {
sm.setShapePropertyBs (this.id, "translucentLevel", Float.$valueOf (this.translucency), this.bsAtoms);
sm.setShapePropertyBs (this.id, "translucency", "translucent", this.bsAtoms);
} else if (this.colors != null) sm.setShapePropertyBs (this.id, "colors", this.colors, this.bsAtoms);
}, "J.modelset.ModelSet,J.util.BS");
$_M(c$, "setPutty", 
($fz = function (sm) {
this.id = 10;
var data =  Clazz.newFloatArray (this.bsAtoms.length (), 0);
this.rd =  new J.atomdata.RadiusData (data, 0, J.atomdata.RadiusData.EnumType.ABSOLUTE, J.constant.EnumVdw.AUTO);
var atoms = sm.viewer.modelSet.atoms;
var sum = 0.0;
var sumsq = 0.0;
var min = 3.4028235E38;
var max = 0;
var n = this.bsAtoms.cardinality ();
for (var i = this.bsAtoms.nextSetBit (0); i >= 0; i = this.bsAtoms.nextSetBit (i + 1)) {
var value = J.modelset.Atom.atomPropertyFloat (null, atoms[i], 1112541199);
sum += value;
sumsq += (value * value);
if (value < min) min = value;
if (value > max) max = value;
}
var mean = (sum / n);
var stdev = Math.sqrt ((sumsq - (sum * sum / n)) / n);
var rad = (this.info)[1];
var range = (this.info)[2];
var scale_min = (this.info)[3];
var scale_max = (this.info)[4];
var power = (this.info)[5];
var transform = Clazz.floatToInt ((this.info)[6]);
var data_range = max - min;
var nonlinear = false;
switch (transform) {
case 0:
case 1:
case 2:
case 3:
nonlinear = true;
break;
}
for (var i = this.bsAtoms.nextSetBit (0), pt = 0; i >= 0; i = this.bsAtoms.nextSetBit (i + 1), pt++) {
var scale = J.modelset.Atom.atomPropertyFloat (null, atoms[i], 1112541199);
switch (transform) {
case 3:
case 7:
default:
break;
case 0:
case 4:
scale = 1 + (scale - mean) / range / stdev;
break;
case 1:
case 5:
scale = (scale - min) / data_range / range;
break;
case 2:
case 6:
scale /= range;
break;
case 8:
if (scale < 0.0) scale = 0.0;
scale = (Math.sqrt (scale / 8.0) / 3.141592653589793);
break;
}
if (scale < 0.0) scale = 0.0;
if (nonlinear) scale = Math.pow (scale, power);
if ((scale < scale_min) && (scale_min >= 0.0)) scale = scale_min;
if ((scale > scale_max) && (scale_max >= 0.0)) scale = scale_max;
data[i] = scale * rad;
}
}, $fz.isPrivate = true, $fz), "J.viewer.ShapeManager");
$_M(c$, "getModelIndex", 
($fz = function (m) {
if (this.bsAtoms == null) return -1;
var iAtom = this.bsAtoms.nextSetBit (0);
return (iAtom < 0 ? -1 : m.atoms[iAtom].modelIndex);
}, $fz.isPrivate = true, $fz), "J.modelset.ModelSet");
$_M(c$, "setColors", 
function (colixes, translucency) {
this.colixes = colixes;
this.colors = [colixes, Float.$valueOf (translucency)];
}, "~A,~N");
$_M(c$, "setSize", 
function (size) {
this.size = Clazz.floatToInt (size * 1000);
}, "~N");
Clazz.defineStatics (c$,
"cPuttyTransformNormalizedNonlinear", 0,
"cPuttyTransformRelativeNonlinear", 1,
"cPuttyTransformScaledNonlinear", 2,
"cPuttyTransformAbsoluteNonlinear", 3,
"cPuttyTransformNormalizedLinear", 4,
"cPuttyTransformRelativeLinear", 5,
"cPuttyTransformScaledLinear", 6,
"cPuttyTransformAbsoluteLinear", 7,
"cPuttyTransformImpliedRMS", 8);
});
