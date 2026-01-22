import { useEffect, useRef } from "react";
import * as THREE from "three";
import { OrbitControls } from "three/examples/jsm/controls/OrbitControls";
import "./MainPage.css";

export default function MainPage() {
  const mountRef = useRef(null);

  useEffect(() => {
    const mount = mountRef.current;
    if (!mount) return;

    const width = mount.clientWidth;
    const height = mount.clientHeight;

    /* ================= SCENE ================= */
    const scene = new THREE.Scene();
    scene.background = null;
    scene.fog = new THREE.FogExp2(0xffffff, 0.0008);

    const camera = new THREE.PerspectiveCamera(40, width / height, 0.1, 2000);
    camera.position.set(0, 340, 720);

    const renderer = new THREE.WebGLRenderer({ antialias: true, alpha: true });
    renderer.setSize(width, height);
    renderer.shadowMap.enabled = true;
    renderer.shadowMap.type = THREE.PCFSoftShadowMap;
    mount.appendChild(renderer.domElement);

    const controls = new OrbitControls(camera, renderer.domElement);
    controls.enableDamping = true;
    controls.enableZoom = false;
    controls.enablePan = false;
    controls.target.set(0, 280, 0);

    /* ================= LIGHT ================= */
    scene.add(new THREE.AmbientLight(0xffffff, 0.7));

    const sun = new THREE.DirectionalLight(0xffffff, 1.0);
    sun.position.set(200, 500, 200);
    sun.castShadow = true;
    scene.add(sun);

    const treeGroup = new THREE.Group();
    scene.add(treeGroup);

    /* ================= TREE TRUNK ================= */
    const trunkHeight = 500;
    const trunk = new THREE.Mesh(
      new THREE.CylinderGeometry(35, 55, trunkHeight, 32),
      new THREE.MeshStandardMaterial({ color: 0x5d4037 })
    );
    trunk.position.y = trunkHeight / 2;
    trunk.castShadow = true;
    treeGroup.add(trunk);

    /* ================= LEAVES ================= */
    const foliageMat = new THREE.MeshStandardMaterial({ color: 0x1b5e20 });

    const createFoliage = (x, y, z, size) => {
      const cluster = new THREE.Group();
      for (let i = 0; i < 15; i++) {
        const leaf = new THREE.Mesh(
          new THREE.SphereGeometry(size, 8, 8),
          foliageMat
        );
        leaf.position.set(
          Math.random() * 30 - 15,
          Math.random() * 20,
          Math.random() * 30 - 15
        );
        leaf.scale.set(1, 0.6, 1);
        cluster.add(leaf);
      }
      cluster.position.set(x, y, z);
      treeGroup.add(cluster);
    };

    for (let i = 0; i < 6; i++) {
      createFoliage(-40, 100 + i * 70, 0, 20 + i * 2);
    }


    /* ================= STAIRS (WIDER) ================= */
    const steps = [];
    for (let i = 0; i < 12; i++) {
      const stepGroup = new THREE.Group();

      const step = new THREE.Mesh(
        // ⬅️ 폭 / 깊이 확장
        new THREE.BoxGeometry(38, 6, 18),
        new THREE.MeshStandardMaterial({ color: 0x8d6e63 })
      );

      const angle = (i / 12) * Math.PI * 1.35;

      // ⬅️ 굵어진 나무 반경(55) + 여유 거리
      const radius = 70;
      const y = i * 38 + 70;

      stepGroup.position.set(
        Math.cos(angle) * radius,
        y,
        Math.sin(angle) * radius
      );

      stepGroup.rotation.y = -angle;
      step.castShadow = true;

      stepGroup.add(step);
      treeGroup.add(stepGroup);
      steps.push(stepGroup);
    }


    /* ================= CHARACTER ================= */
    const char = new THREE.Group();
    const body = new THREE.Mesh(
      new THREE.BoxGeometry(10, 15, 7),
      new THREE.MeshStandardMaterial({ color: 0x1565c0 })
    );
    body.position.y = 7.5;

    const head = new THREE.Mesh(
      new THREE.BoxGeometry(9, 9, 9),
      new THREE.MeshStandardMaterial({ color: 0xffccbc })
    );
    head.position.y = 19;

    char.add(body, head);

    if (steps[6]) {
      char.position.copy(steps[6].position);

      // ⬆️ 캐릭터를 계단 위로 올림
      char.position.y += 12;

      // ⬅️ 계단 바깥쪽으로 살짝 이동 (나무에서 분리)
      const offset = 14;
      char.position.x += Math.cos(steps[6].rotation.y) * offset;
      char.position.z += Math.sin(steps[6].rotation.y) * offset;

      // ⬅️ 나무 쪽을 바라보게
      char.rotation.y = steps[6].rotation.y + Math.PI / 2;
    }

    treeGroup.add(char);


    /* ================= CLOUDS (TOP ONLY) ================= */
    const cloudMat = new THREE.MeshStandardMaterial({
      color: 0xffffff,
      transparent: true,
      opacity: 0.8,
    });

    const clouds = [];

    const createCloud = (x, y, z, scale = 1) => {
      const group = new THREE.Group();
      const count = 6;

      for (let i = 0; i < count; i++) {
        const puff = new THREE.Mesh(
          new THREE.SphereGeometry(22, 8, 8),
          cloudMat
        );
        puff.position.set(i * 18, Math.random() * 10, Math.random() * 10);
        puff.scale.set(1, 0.6, 1);
        group.add(puff);
      }

      group.position.set(x, y, z);
      group.scale.setScalar(scale);
      scene.add(group);
      clouds.push(group);
    };

    /* 🌥 상단에만 배치 */
      createCloud(-180, 480, -120, 1.3);
      createCloud(0, 500, 0, 1.5);
      createCloud(180, 490, 100, 1.2);

    /* ================= ANIMATION ================= */
    const animate = () => {
      requestAnimationFrame(animate);

      clouds.forEach((c, i) => {
        c.position.x += 0.04 + i * 0.01;
        if (c.position.x > 260) c.position.x = -260;
      });

      controls.update();
      renderer.render(scene, camera);
    };
    animate();

    const handleResize = () => {
      renderer.setSize(mount.clientWidth, mount.clientHeight);
      camera.aspect = mount.clientWidth / mount.clientHeight;
      camera.updateProjectionMatrix();
    };
    window.addEventListener("resize", handleResize);

    return () => {
      window.removeEventListener("resize", handleResize);
      mount.removeChild(renderer.domElement);
    };
  }, []);


  
  return (
    <div className="dashboard-container">
      {/* 왼쪽 컬럼 */}
      <div className="column left-col">
        <section className="card profile-card">
          <div className="profile-header">
            <div className="avatar-circle"></div>
            <div className="profile-info">
              <span className="user-name">방문자</span>
              <span className="level-badge">Lv.6 모험가</span>
              <button className="read-btn">READ (+Exp)</button>
            </div>
          </div>
          <div className="exp-section">
            <div className="exp-label"><span>EXP</span><span>700 / 1400</span></div>
            <div className="progress-bar-bg"><div className="progress-bar-fill" style={{ width: "50%" }}></div></div>
          </div>
          <div className="stats-row">
            <div className="stat-box">📖 0권</div>
            <div className="stat-box">⏰ 0분</div>
          </div>
        </section>

        <section className="card">
          <h3 className="card-title">🎧 현재 참여 가능한 TTS 룸</h3>
          <div className="tts-list">
            {["해리포터 정주행 팟", "자기전 시 낭송", "코스모스 함께 읽기"].map((r, i) => (
              <div key={i} className="tts-item">{r} <span className="play-icon">▶</span></div>
            ))}
          </div>
          <button className="add-btn">+ 방 만들기</button>
        </section>

        <section className="card">
          <h3 className="card-title">📜 일일 퀘스트</h3>
          <ul className="quest-list">
            <li className="done">✔ 로그인 하기 <span>+50</span></li>
            <li>○ 30분 독서하기 <span className="active">+100</span></li>
            <li>○ 커뮤니티 글 1개 쓰기 <span className="active">+30</span></li>
          </ul>
        </section>

        <section className="card">
            <h3 className="card-title">🏆 내 배지</h3>
            <div className="badge-grid">
                {["🌱","📚","🌙","✍️","🏰","👤","💯","👑"].map((b,i)=>(
                    <div key={i} className={`badge-item ${i<4?'active':''}`}>{b}</div>
                ))}
            </div>
        </section>
      </div>

      {/* 중앙 컬럼 (3D 캔버스 영역) */}
      <div className="column center-col">
        <div className="tree-card-wrapper">
          <div className="tree-header">
            <h1 className="main-logo">성장의 나무</h1>
            <p className="sub-logo">Cycle Lv.6 / 10</p>
          </div>
          <div className="canvas-area">
            <div className="three-canvas-container" ref={mountRef}></div>
            <div className="level-ruler">
              {[10, 9, 8, 7, 6, 5, 4, 3, 2, 1].map((n) => (
                <div key={n} className={`level-mark ${n === 6 ? "current" : ""}`}>
                  {n} <span className="dash">—</span>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* 오른쪽 컬럼 (도서 목록 가로 4개) */}
      <div className="column right-col">
        <section className="card news-card">
          <h3 className="card-title">📬 이웃 소식</h3>
          <div className="notice-list">
            <div className="notice-item"><b>책벌레</b>님이 완독했습니다. <small>10분 전</small></div>
            <div className="notice-item"><b>모험왕</b>님이 레벨 5 달성! <small>30분 전</small></div>
          </div>
        </section>

        <div className="book-section-container">
            <div className="section-header">
                <h3>📚 추천 도서</h3>
                <span className="more">서재 가기 →</span>
            </div>
            
            <BookGrid4 title="🔥 지금 가장 핫한 책" />
            <BookGrid4 title="🤖 모험가님을 위한 AI 추천" />
            <BookGrid4 title="⚔️ 판타지 장르 베스트" />
            <BookGrid4 title="✨ 이번 주 신간" />
        </div>
      </div>
    </div>
  );
}

function BookGrid4({ title }) {
    return (
        <div className="book-shelf">
            <h4 className="shelf-title">{title}</h4>
            <div className="grid-4">
                {[1,2,3,4].map(i => (
                    <div key={i} className="book-card-mini">
                        <div className="cover">?</div>
                        <p className="name">도서 {i}</p>
                        <span className="author">작가</span>
                    </div>
                ))}
            </div>
        </div>
    )
}