var scene = new THREE.Scene();
var camera = new THREE.PerspectiveCamera(75, window.innerWidth / window.innerHeight, 0.1, 1000);

var renderer = new THREE.WebGLRenderer();
renderer.setSize(window.innerWidth, window.innerHeight);
document.body.appendChild(renderer.domElement);

var geometry = new THREE.BoxGeometry(1, 1, 1);
var material = new THREE.MeshBasicMaterial({color: 0x808000, transparent:true, opacity:0.5});
var cube = new THREE.Mesh(geometry, material);
scene.add(cube);

camera.position.z = 5;
console.log(cube);

var animate = function () {
    requestAnimationFrame(animate);

    cube.position.z += 0.01;
    cube.rotation.x += 0.1;
    cube.rotation.y += 0.1;

    renderer.render(scene, camera);
};

animate();