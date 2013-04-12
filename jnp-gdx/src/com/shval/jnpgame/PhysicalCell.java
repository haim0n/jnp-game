package com.shval.jnpgame;

import static com.shval.jnpgame.Globals.*;
import static com.shval.jnpgame.Globals.RIGHT;
import static com.shval.jnpgame.Globals.UP;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;


class PhysicalCell implements Disposable{
	
	private static final String TAG = PhysicalCell.class.getSimpleName();
	World world;
	Body bodies[][];
	Texture texture;
	Texture anchorTextures[];
	Mesh mesh;
	Mesh anchorMeshes[];
	private Vector2[][] buttomLeft;
	static private Vector2 textureSize;
	private static Vector2 physicalSize;

	
	public PhysicalCell(Vector2 pos, Texture texture, World world, BodyType type) {

		Gdx.app.debug(TAG, "Creating phy-cell at " + pos.x + ", " + pos.y);
		// create 3 x 3 mesh

		anchorMeshes = new Mesh[4];
		this.world = world;
		this.texture = texture;
		bodies = new Body[3][3];

		FixtureDef fd = new FixtureDef();
		
		PolygonShape shape = new PolygonShape();
		shape.setAsBox(0.95f * physicalSize.x / 6, 0.95f * physicalSize.y / 6);
		//CircleShape shape = new CircleShape();
		//shape.setRadius(r);
		
		fd.shape = shape;
		fd.density = 1f;
		fd.friction = 0.0f;
		fd.restitution = 0.0f;
		
		BodyDef bd = new BodyDef();
		bd.type = BodyType.StaticBody; // bodies will turn dynamic when needed
		bd.fixedRotation = true; // TODO: true??
		
		// body vertices
		float x = pos.x + physicalSize.x / 6;
		float y = pos.y + physicalSize.y / 6;
		float dx = physicalSize.x / 3;
		float dy = physicalSize.y / 3;
		Body body;
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (i * j == 1)
					continue;
				bd.position.set(x + i * dx, y + j * dy);
				body = world.createBody(bd);
				body.createFixture(fd);
				bodies[i][j]= body;
			}
		}

		shape.setAsBox(physicalSize.x / 20, physicalSize.y / 20);
		bd.position.set(x + 1 * dx, y + 1 * dy);
		body = world.createBody(bd);
		body.createFixture(fd);
		bodies[1][1]= body;
		
		
		
		// edges
		if (type != BodyType.DynamicBody)
			return;
		
		int n = 3;
		float dr = (float) Math.sqrt(dx * dx + dy * dy);
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				body = bodies[i][j];
				Body body2;
				if (i < n - 1) {
					body2 = bodies[i+1][j];
					join(body, body2, dx);
				}
				
				if (j < n - 1) {
					body2 = bodies[i][j+1];
					join(body, body2, dy);
				}
				
				if (i < n - 1 && j < n - 1) {
					body2 = bodies[i + 1][j + 1];
					join(body, body2, dr);
				}
				
				if (i < n - 1 && j > 0) {
					body2 = bodies[i + 1][j - 1];
					join(body, body2, dr);
				}					
			}
		}
		
		shape.dispose();
	} // constructor
	
	
	void destroy() {
		Gdx.app.debug(TAG, "Destroying phy-cell ");
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (bodies[i][j] != null) {
					world.destroyBody(bodies[i][j]);
				}	
			}
		}
	}
	
	void setTextureBL(Vector2 bl[][]) {
		this.buttomLeft = bl; // 2 x 2 vector of bottom left texture coords for 1/4 cell
	}
	
	public void setAnchors(Texture[] anchors) {
		this.anchorTextures = anchors;
	}


	static public void SetSize(Vector2 textureSize, Vector2 physicalSize) {
		PhysicalCell.textureSize = textureSize; // size of each 1/4 cell texture
		PhysicalCell.physicalSize = physicalSize; 
	}
	
	private void buildAnchorMeshs() {
		short indices[] = {0, 1, 2, 2, 3, 0};
		if (anchorTextures == null)
			return;
		// left 
		if (anchorTextures[LEFT] != null) {
			// we will a rectangle
			Mesh anchorMesh = new Mesh(true, 4 * 4, 3 * 2, 
		            new VertexAttribute(Usage.Position, 2, "a_position"),
		            new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
			
			float vertices[] = new float[4 * 4];
			
			
			// from bottom left corner, counter clockwise
			int v;
			float xTex = 27;
			float dxTex = 24;
			float yTex = 30;
			float dyTex = 18;
			
			Vector2 pos = bodies[0][1].getPosition();
			v = 0;
			
			vertices[4 * (v + 0) + 0] = pos.x - 1.1f * physicalSize.x / 6; 
			vertices[4 * (v + 0) + 1] = pos.y - physicalSize.y / 6;
			vertices[4 * (v + 0) + 2] = (xTex)/256f; 
			vertices[4 * (v + 0) + 3] = (yTex + dyTex)/256f;
			v++;
			
			vertices[4 * (v + 0) + 0] = pos.x + physicalSize.x / 3; 
			vertices[4 * (v + 0) + 1] = pos.y - physicalSize.y / 6;
			vertices[4 * (v + 0) + 2] = (xTex + dxTex)/256f; 
			vertices[4 * (v + 0) + 3] = (yTex + dyTex)/256f;
			v++;

			vertices[4 * (v + 0) + 0] = pos.x + physicalSize.x / 3; 
			vertices[4 * (v + 0) + 1] = pos.y + physicalSize.y / 6;
			vertices[4 * (v + 0) + 2] = (xTex + dxTex)/256f; 
			vertices[4 * (v + 0) + 3] = (yTex)/256f;
			v++;
			
			vertices[4 * (v + 0) + 0] = pos.x - 1.1f * physicalSize.x / 6; 
			vertices[4 * (v + 0) + 1] = pos.y + physicalSize.y / 6;
			vertices[4 * (v + 0) + 2] = (xTex)/256f; 
			vertices[4 * (v + 0) + 3] = (yTex+ 0f)/256f;
			
			anchorMesh.setVertices(vertices);
			anchorMesh.setIndices(indices);
			anchorMeshes[LEFT] = anchorMesh;
		}
		 
		// right  
		if (anchorTextures[RIGHT] != null) {
			// we will a rectangle
			Mesh anchorMesh = new Mesh(true, 4 * 4, 3 * 2, 
		            new VertexAttribute(Usage.Position, 2, "a_position"),
		            new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
			
			float vertices[] = new float[4 * 4];
			
			
			// from bottom left corner, counter clockwise
			int v;
			float xTex = 1;
			float dxTex = 24;
			float yTex = 5;
			float dyTex = 18;
			
			Vector2 pos = bodies[2][1].getPosition();
			v = 0;
			
			vertices[4 * (v + 0) + 0] = pos.x - physicalSize.x / 3; 
			vertices[4 * (v + 0) + 1] = pos.y - physicalSize.y / 6;
			vertices[4 * (v + 0) + 2] = (xTex)/256f; 
			vertices[4 * (v + 0) + 3] = (yTex + dyTex)/256f;
			v++;
			
			vertices[4 * (v + 0) + 0] = pos.x + 1.1f * physicalSize.x / 6; 
			vertices[4 * (v + 0) + 1] = pos.y - physicalSize.y / 6;
			vertices[4 * (v + 0) + 2] = (xTex + dxTex)/256f; 
			vertices[4 * (v + 0) + 3] = (yTex + dyTex)/256f;
			v++;

			vertices[4 * (v + 0) + 0] = pos.x + 1.1f * physicalSize.x / 6; 
			vertices[4 * (v + 0) + 1] = pos.y + physicalSize.y / 6;
			vertices[4 * (v + 0) + 2] = (xTex + dxTex)/256f; 
			vertices[4 * (v + 0) + 3] = (yTex)/256f;
			v++;
			
			vertices[4 * (v + 0) + 0] = pos.x - physicalSize.x / 3; 
			vertices[4 * (v + 0) + 1] = pos.y + physicalSize.y / 6;
			vertices[4 * (v + 0) + 2] = (xTex)/256f; 
			vertices[4 * (v + 0) + 3] = (yTex+ 0f)/256f;
			
			anchorMesh.setVertices(vertices);
			anchorMesh.setIndices(indices);
			anchorMeshes[RIGHT] = anchorMesh;
		}
		
		// up  
		if (anchorTextures[UP] != null) {
			// we will a rectangle
			Mesh anchorMesh = new Mesh(true, 4 * 4, 3 * 2, 
		            new VertexAttribute(Usage.Position, 2, "a_position"),
		            new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
			
			float vertices[] = new float[4 * 4];
			
			
			// from bottom left corner, counter clockwise
			int v;
			float xTex = 4;
			float dxTex = 18;
			float yTex = 27;
			float dyTex = 24;
			
			Vector2 pos = bodies[1][2].getPosition();
			v = 0;
			
			vertices[4 * (v + 0) + 0] = pos.x - physicalSize.x / 6; 
			vertices[4 * (v + 0) + 1] = pos.y - physicalSize.y / 3;
			vertices[4 * (v + 0) + 2] = (xTex)/256f; 
			vertices[4 * (v + 0) + 3] = (yTex + dyTex)/256f;
			v++;
			
			vertices[4 * (v + 0) + 0] = pos.x + physicalSize.x / 6; 
			vertices[4 * (v + 0) + 1] = pos.y - physicalSize.y / 3;
			vertices[4 * (v + 0) + 2] = (xTex + dxTex)/256f; 
			vertices[4 * (v + 0) + 3] = (yTex + dyTex)/256f;
			v++;

			vertices[4 * (v + 0) + 0] = pos.x + physicalSize.x / 6; 
			vertices[4 * (v + 0) + 1] = pos.y + 1.1f * physicalSize.y / 6;
			vertices[4 * (v + 0) + 2] = (xTex + dxTex)/256f; 
			vertices[4 * (v + 0) + 3] = (yTex)/256f;
			v++;
			
			vertices[4 * (v + 0) + 0] = pos.x - physicalSize.x / 6; 
			vertices[4 * (v + 0) + 1] = pos.y + 1.1f * physicalSize.y / 6;
			vertices[4 * (v + 0) + 2] = (xTex)/256f; 
			vertices[4 * (v + 0) + 3] = (yTex+ 0f)/256f;
			
			anchorMesh.setVertices(vertices);
			anchorMesh.setIndices(indices);
			anchorMeshes[UP] = anchorMesh;
		}

		// down
		if (anchorTextures[DOWN] != null) {
			// we will a rectangle
			Mesh anchorMesh = new Mesh(true, 4 * 4, 3 * 2, 
		            new VertexAttribute(Usage.Position, 2, "a_position"),
		            new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
			
			float vertices[] = new float[4 * 4];
			
			
			// from bottom left corner, counter clockwise
			int v;
			float xTex = 30;
			float dxTex = 18;
			float yTex = 1;
			float dyTex = 24;
			
			Vector2 pos = bodies[1][0].getPosition();
			v = 0;
			
			vertices[4 * (v + 0) + 0] = pos.x - physicalSize.x / 6; 
			vertices[4 * (v + 0) + 1] = pos.y - 1.1f * physicalSize.y / 6;
			vertices[4 * (v + 0) + 2] = (xTex)/256f; 
			vertices[4 * (v + 0) + 3] = (yTex + dyTex)/256f;
			v++;
			
			vertices[4 * (v + 0) + 0] = pos.x + physicalSize.x / 6; 
			vertices[4 * (v + 0) + 1] = pos.y - 1.1f * physicalSize.y / 6;
			vertices[4 * (v + 0) + 2] = (xTex + dxTex)/256f; 
			vertices[4 * (v + 0) + 3] = (yTex + dyTex)/256f;
			v++;

			vertices[4 * (v + 0) + 0] = pos.x + physicalSize.x / 6; 
			vertices[4 * (v + 0) + 1] = pos.y + physicalSize.y / 3;
			vertices[4 * (v + 0) + 2] = (xTex + dxTex)/256f; 
			vertices[4 * (v + 0) + 3] = (yTex)/256f;
			v++;
			
			vertices[4 * (v + 0) + 0] = pos.x - physicalSize.x / 6; 
			vertices[4 * (v + 0) + 1] = pos.y + physicalSize.y / 3;
			vertices[4 * (v + 0) + 2] = (xTex)/256f; 
			vertices[4 * (v + 0) + 3] = (yTex+ 0f)/256f;
			
			anchorMesh.setVertices(vertices);
			anchorMesh.setIndices(indices);
			anchorMeshes[DOWN] = anchorMesh;
		}
	}
	
	private void buildMesh() { 
		disposeMesh();
		mesh = new Mesh(true, 4 * 4 * 4, 3 * 2 * 2 * 2, 
	            new VertexAttribute(Usage.Position, 2, "a_position"),
	            new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));
	
		float vertices[] = new float[4 * 4 * 4];
		short indices[] = new short[3 * 2 * 2 * 2];
	
		Vector2 pos, tex;
		int v = 0;
		int ind = 0;
		
		// 4 x 1/4 cell
		
		for (int i = 0; i < 2; i++) {
			for (int j = 0; j < 2; j++) {
				tex = buttomLeft[i][j];
				
				pos = bodies[i][j].getPosition();
				vertices[4 * (v + 0) + 0] = pos.x - (1 - i) * physicalSize.x / 6; 
				vertices[4 * (v + 0) + 1] = pos.y - (1 - j) * physicalSize.y / 6;
				vertices[4 * (v + 0) + 2] = tex.x; 
				vertices[4 * (v + 0) + 3] = tex.y;
				
				pos = bodies[i + 1][j].getPosition();
				vertices[4 * (v + 1) + 0] = pos.x - (0 - i) * physicalSize.x / 6;
				vertices[4 * (v + 1) + 1] = pos.y - (1 - j) * physicalSize.y / 6;
				vertices[4 * (v + 1) + 2] = tex.x + textureSize.x; 
				vertices[4 * (v + 1) + 3] = tex.y;
	
				pos = bodies[i + 1][j + 1].getPosition();
				vertices[4 * (v + 2) + 0] = pos.x - (0 - i) * physicalSize.x / 6;
				vertices[4 * (v + 2) + 1] = pos.y - (0 - j) * physicalSize.y / 6;
				vertices[4 * (v + 2) + 2] = tex.x + textureSize.x; 
				vertices[4 * (v + 2) + 3] = tex.y - textureSize.y; // rrrr!!					
				
				pos = bodies[i][j + 1].getPosition();
				vertices[4 * (v + 3) + 0] = pos.x - (1 - i) * physicalSize.x / 6;
				vertices[4 * (v + 3) + 1] = pos.y - (0 - j) * physicalSize.y / 6;
				vertices[4 * (v + 3) + 2] = tex.x; 
				vertices[4 * (v + 3) + 3] = tex.y - textureSize.y; // rrrr!!					
				
				// two triangles
				indices[ind++] = (short) v;
				indices[ind++] = (short) (v + 1);
				indices[ind++] = (short) (v + 2);
				
				indices[ind++] = (short) v;
				indices[ind++] = (short) (v + 2);
				indices[ind++] = (short) (v + 3);
				
				v += 4;
	
			}
		}
		
		//Gdx.app.error("", "x = " + vertices[0] + " , y = " + vertices[1]);
		
		mesh.setVertices(vertices);
		mesh.setIndices(indices);
		
	} // buildMesh

	void render()  {
		
		//Gdx.app.debug(TAG, "rendering phy-cell at " + bodies[1][1].getPosition().x);
		
		if (buttomLeft == null)
			return;
		
		
		buildMesh();
		GL10 gl = Gdx.graphics.getGL10();
		//Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        
		
        texture.bind();
        gl.glPushMatrix();
       // gl.glTranslatef(0.3f, 0.0f, 0.0f);
        //gl.glScalef(1f, 1f, 1.0f);
        //gl.glRotatef(0, 0f, 0f, 1f);
        mesh.render(GL10.GL_TRIANGLES);
        gl.glPopMatrix();
        
	}
	
	void renderAnchors() {
		disposeAnchors();
		buildAnchorMeshs();
		GL10 gl = Gdx.graphics.getGL10();

		for (int i = 0; i < 4; i++) {
        	if (anchorMeshes[i] != null) {
        		anchorTextures[i].bind();
                gl.glPushMatrix();
                //gl.glTranslatef(0.3f, 0.0f, 0.0f);
                //gl.glScalef(1f, 1f, 1.0f);
                
                gl.glRotatef(0, 0f, 0f, 1f);
                anchorMeshes[i].render(GL10.GL_TRIANGLES);
                gl.glPopMatrix();        		
        	}
        }
	}
	private void join(Body b1, Body b2, float length) {
		
		DistanceJointDef jd = new DistanceJointDef(); 
		jd.localAnchorA.set(0, 0);
		jd.localAnchorB.set(0, 0);
		jd.bodyA = b1;
		jd.bodyB = b2;
		jd.collideConnected = true;
		//jd.maxLength = 10 + 2 * i;
		jd.length = length;
		//jd.maxForce = mass * gravity;
		//jd.maxTorque = mass * radius * gravity;
		jd.dampingRatio = 0f;
		jd.frequencyHz = 10;
		world.createJoint(jd);
	}
	
	private void fix(Body b1, Body b2, /*float length,*/ int dir, int fixPoints) {
		
		DistanceJointDef jd = new DistanceJointDef();
		jd.bodyA = b1;
		jd.bodyB = b2;
		

		jd.collideConnected = false; // TODO??
		//jd.maxLength = 10 + 2 * i;
		jd.length = 0; // 0.9f * length;
		//jd.maxForce = mass * gravity;
		//jd.maxTorque = mass * radius * gravity;
		jd.dampingRatio = 1f; // TODO
		jd.frequencyHz = 10f;
		
		float dx = 0;
		float dy = 0;

		switch (dir) {
		case UP:
			dy = physicalSize.y / 6 * 0.75f;
			float ddx = (physicalSize.x / 3) / (fixPoints + 1); 
			for (int i = 1; i <= fixPoints; i++) {
				dx = - (physicalSize.x / 6) + i * ddx;
				jd.localAnchorA.set(dx, dy);
				jd.localAnchorB.set(dx, -dy);
				world.createJoint(jd);
			}
			
			break;
		case RIGHT:
			dx = physicalSize.x / 6 * 0.75f;
			float ddy = (physicalSize.y / 3) / (fixPoints + 1); 
			for (int i = 1; i <= fixPoints; i++) {
				dy = - (physicalSize.x / 6) + i * ddy;
				jd.localAnchorA.set(dx, dy);
				jd.localAnchorB.set(-dx, dy);
				world.createJoint(jd);
			}
			break;
		default:
			Gdx.app.error(TAG, "fix - should not be here");
		}
		

	}
	
	void merge(PhysicalCell peer, int dir) {
		
		DistanceJointDef jd = new DistanceJointDef();
		jd.length = 0.95f * (physicalSize.x / 3);
		jd.collideConnected = false;
		//jd.dampingRatio = 0.5f;
		//jd.frequencyHz = 6;
		jd.localAnchorA.set(0, 0);
		jd.localAnchorB.set(0, 0);
		
		if (dir == 1) { // right TODO: fix it

			
			jd.bodyA = this.bodies[2][0];
			jd.bodyB = peer.bodies[0][0];
			world.createJoint(jd);
			
			jd.bodyA = this.bodies[2][1];
			jd.bodyB = peer.bodies[0][1];
			world.createJoint(jd);
			
			jd.bodyA = this.bodies[2][2];
			jd.bodyB = peer.bodies[0][2];
			world.createJoint(jd);
		}
	}
	
	public void applyForce(int i, int j, Vector2 force) {
		bodies[i][j].applyForce(force, new Vector2(0, 0));
	}

	public void applyLinearImpulse(int i, int j, Vector2 force) {
		bodies[i][j].applyLinearImpulse(force, new Vector2(0, 0));
	}
	
	public void setType(int i, int j, BodyType type) {
		bodies[i][j].setType(type);
	}


	public void fixPeer(PhysicalCell peer, int dir) {
		Gdx.app.debug(TAG, "Trying to fix peer from dir " + dir);
		if (dir == RIGHT) {
			fix(bodies[2][0], peer.bodies[0][0], /*physicalSize.x / 3 ,*/ dir, 20);
			fix(bodies[2][1], peer.bodies[0][1], dir, 20);
			fix(bodies[2][2], peer.bodies[0][2], dir, 20);
		} else if (dir == UP) {
			fix(bodies[0][2], peer.bodies[0][0], /* physicalSize.y / 3 , */ dir, 3);
			fix(bodies[1][2], peer.bodies[1][0], /* physicalSize.y / 3, */ dir, 3);
			fix(bodies[2][2], peer.bodies[2][0], /* physicalSize.y / 3, */ dir, 3);
		} else {
			Gdx.app.error(TAG, "Trying to fix peer from dir " + dir + ". Should only fix RIGHT or UP peers");
		}
	}

	public void anchorPeer(PhysicalCell peer, int dir) {
		Gdx.app.debug(TAG, "Trying to anchor peer from dir " + dir);
		if (dir == RIGHT) {
			fix(bodies[2][1], peer.bodies[0][1], /*physicalSize.x / 3,*/ dir, 1);
		} else if (dir == UP) {
			fix(bodies[1][2], peer.bodies[1][0], /*physicalSize.y / 3,*/ dir, 1);
		} else {
			Gdx.app.error(TAG, "Trying to anchor peer from dir " + dir + ". Should only fix RIGHT or UP peers");
		}
	}
	
	private void setDynamic() {
		for (int i = 0; i < 3; i++)
			for (int j = 0; j < 3; j++)
				bodies[i][j].setType(BodyType.DynamicBody);
	}
	
	public void move(int dir) {
		
		Gdx.app.debug(TAG, "Applying impulse. dir = " + dir);
		float dx = 0;
		
		if (dir == LEFT || dir == RIGHT) {
			if (dir == LEFT)
				dx = -physicalSize.x;
			else
				dx = physicalSize.x;
		
			// move by joining to virtual point
			
			BodyDef bd = new BodyDef();
			for (int i = 0; i <3 ; i++) {
				Body src = bodies[i][0];
				bd.position.set(src.getPosition().x  + dx, src.getPosition().y);
				Body dst = world.createBody(bd);
				join(src, dst, 0);
			}
			for (int i = 0; i <3 ; i++) {
				Body src = bodies[i][2];
				bd.position.set(src.getPosition().x  + dx, src.getPosition().y);
				Body dst = world.createBody(bd);
				join(src, dst, 0);
			}
		}
		else if (dir == DOWN) {
			// gravity will take it from here
		}
		else if (dir == UP) {
			// TODO: not implemented yet
		}
		else {
			Gdx.app.error(TAG, "moving to unsupported direction");
			return;
		}

		
		setDynamic();
		
		/*
		switch(dir) {
		case LEFT:
			physicalSize.x
			bodies[0][1].applyForce(new Vector2(-100000f, 0), new Vector2(0, 0));
			bodies[0][1].setLinearVelocity(-10000, 0);
			break;
		case RIGHT:
			bodies[2][1].applyForce(new Vector2(+100000f, 0), new Vector2(0, 0));
			bodies[2][1].setLinearVelocity(+10000, 0);
			break;
			
		case DOWN:
			break; //gravity is in charge here
		case UP:
			// TODO
			
		default:
			Gdx.app.error(TAG, "Trying to move physical cell in invalid direction" + dir);

		}
		*/

	}

	void disposeAnchors() {		
		for (int i = 0; i <4; i++)
			if (anchorMeshes[i] != null)
				anchorMeshes[i].dispose();
	}
	
	
	void disposeMesh() {
		if (mesh != null) {
			mesh.dispose();
			mesh = null;
		}
	}
	
	@Override
	public void dispose() {
		disposeMesh();
		disposeAnchors();
	}

}

