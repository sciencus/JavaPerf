package org.polytechtours.javaperformance.tp.paintingants;
// package PaintingAnts_v3;
// version : 4.0

import java.awt.Color;
import java.util.Random;

public class CFourmi {
  // Tableau des incrémentations à effectuer sur la position des fourmis
  // en fonction de la direction du deplacement
  static private int[][] mIncDirection = new int[8][2];
  // le generateur aléatoire (Random est thread safe donc on la partage)
  private static Random GenerateurAleatoire = new Random();
  // couleur déposé par la fourmi
  private Color mCouleurDeposee;
  private float mLuminanceCouleurSuivie;
  // objet graphique sur lequel les fourmis peuvent peindre
  private CPainting mPainting;
  // Coordonées de la fourmi
  private int x, y;
  // Proba d'aller a gauche, en face, a droite, de suivre la couleur
  private float[] mProba = new float[4];
  // Numéro de la direction dans laquelle la fourmi regarde
  private int mDirection;
  // Taille de la trace de phéromones déposée par la fourmi
  private int mTaille;
  // Pas d'incrémentation des directions suivant le nombre de directions
  // allouées à la fourmies
  private int mDecalDir;
  // l'applet
  private PaintingAnts mApplis;
  // seuil de luminance pour la détection de la couleur recherchée
  private float mSeuilLuminance;
  // nombre de déplacements de la fourmi
  private long mNbDeplacements;

  /*************************************************************************************************
  */
  public CFourmi(Color pCouleurDeposee, Color pCouleurSuivie, float pProbaTD, float pProbaG, float pProbaD,
      float pProbaSuivre, CPainting pPainting, char pTypeDeplacement, float pInit_x, float pInit_y, int pInitDirection,
      int pTaille, float pSeuilLuminance, PaintingAnts pApplis) {

    mCouleurDeposee = pCouleurDeposee;
    mLuminanceCouleurSuivie = 0.2426f * pCouleurDeposee.getRed() + 0.7152f * pCouleurDeposee.getGreen()
        + 0.0722f * pCouleurDeposee.getBlue();
    mPainting = pPainting;
    mApplis = pApplis;

    // direction de départ
    mDirection = pInitDirection;

    // taille du trait
    mTaille = pTaille;

    // initialisation des probas
    mProba[0] = pProbaG; // proba d'aller à gauche
    mProba[1] = pProbaTD; // proba d'aller tout droit
    mProba[2] = pProbaD; // proba d'aller à droite
    mProba[3] = pProbaSuivre; // proba de suivre la couleur

    // nombre de directions pouvant être prises : 2 types de déplacement
    // possibles
    if (pTypeDeplacement == 'd') {
      mDecalDir = 2;
    } else {
      mDecalDir = 1;
    }

    // initialisation du tableau des directions
    CFourmi.mIncDirection[0][0] = 0;
    CFourmi.mIncDirection[0][1] = -1;
    CFourmi.mIncDirection[1][0] = 1;
    CFourmi.mIncDirection[1][1] = -1;
    CFourmi.mIncDirection[2][0] = 1;
    CFourmi.mIncDirection[2][1] = 0;
    CFourmi.mIncDirection[3][0] = 1;
    CFourmi.mIncDirection[3][1] = 1;
    CFourmi.mIncDirection[4][0] = 0;
    CFourmi.mIncDirection[4][1] = 1;
    CFourmi.mIncDirection[5][0] = -1;
    CFourmi.mIncDirection[5][1] = 1;
    CFourmi.mIncDirection[6][0] = -1;
    CFourmi.mIncDirection[6][1] = 0;
    CFourmi.mIncDirection[7][0] = -1;
    CFourmi.mIncDirection[7][1] = -1;

    mSeuilLuminance = pSeuilLuminance;
    mNbDeplacements = 0;
  }

  /*************************************************************************************************
   * Titre : void deplacer() Description : Fonction de deplacement de la fourmi
   *
   */
  public void deplacer() {
	    float tirage, prob1, prob2, prob3, total;
	    int[] dir = new int[3];
	    int i1, j1, i2, j2, i3, j3;
	    int Couleur1, Couleur2, Couleur3;

	    mNbDeplacements++;

	    dir[0] = 0;
	    dir[1] = 0;
	    dir[2] = 0;
		
		int A;
	    int Largeur = mPainting.getLargeur();
	    int Hauteur = mPainting.getHauteur();
	        
	    A = (mDirection - mDecalDir) & 0007;
	    i1 = modulo(x + CFourmi.mIncDirection[A][0], Largeur);
	    j1 = modulo(y + CFourmi.mIncDirection[A][1], Hauteur);
		
		A = mDirection & 0007;
	    i2 = modulo(x + CFourmi.mIncDirection[A][0], Largeur);
	    j2 = modulo(y + CFourmi.mIncDirection[A][1], Hauteur);
		
		A = (mDirection + mDecalDir) & 0007;
	    i3 = modulo(x + CFourmi.mIncDirection[A][0], Largeur);
	    j3 = modulo(y + CFourmi.mIncDirection[A][1], Hauteur);
	    
	    if(mApplis.mBaseImage != null)
			{
				Couleur1 = mApplis.mBaseImage.getRGB(i1, j1);
				Couleur2 = mApplis.mBaseImage.getRGB(i3, j2);
				Couleur3 = mApplis.mBaseImage.getRGB(i3, j3);		
			}
	    else 
	    {
			Couleur1 = mPainting.getCouleur(i1, j1).getRGB();
			Couleur2 = mPainting.getCouleur(i2, j2).getRGB();
			Couleur3 = mPainting.getCouleur(i3, j3).getRGB();
		}
		
	    dir[0] = (testCouleur(Couleur1)) ? 1 : 0;
		dir[1] = (testCouleur(Couleur2)) ? 1 : 0;
		dir[2] = (testCouleur(Couleur3)) ? 1 : 0;
		
		// pas besoin de générer un nombre aléatoire en thread safe
		tirage = (float)Math.random();

	    // la fourmi suit la couleur
	    if (((tirage <= mProba[3]) && ((dir[0] + dir[1] + dir[2]) > 0)) || ((dir[0] + dir[1] + dir[2]) == 3))
	    {
	      prob1 = (dir[0]) * mProba[0];
	      prob2 = (dir[1]) * mProba[1];
	      prob3 = (dir[2]) * mProba[2];
	    }
	    // la fourmi ne suit pas la couleur
	    else {
	      prob1 = (1 - dir[0]) * mProba[0];
	      prob2 = (1 - dir[1]) * mProba[1];
	      prob3 = (1 - dir[2]) * mProba[2];
	    }
	    total = prob1 + prob2 + prob3;
	    prob1 = prob1 / total;
	    prob2 = prob2 / total + prob1;
	    prob3 = prob3 / total + prob2;

	    // incrÃ©mentation de la direction de la fourmi selon la direction choisie
	    // pas besoin de générer un nombre aléatoire en thread safe
		tirage = (float)Math.random();
		
	    if (tirage < prob1) {
	      mDirection = modulo(mDirection - mDecalDir, 8);
	    } else {
	      if (tirage < prob2) {
	        /* rien, on va tout droit */
	      } else {
	        mDirection = A; // modulo(mDirection + mDecalDir, 8);
	      }
	    }

	    x += CFourmi.mIncDirection[mDirection][0];
	    y += CFourmi.mIncDirection[mDirection][1];

	    x = modulo(x, Largeur);
	    y = modulo(y, Hauteur);

	    // coloration de la nouvelle position de la fourmi
	    mPainting.setCouleur(x, y, mCouleurDeposee, mTaille);

	    mApplis.IncrementFpsCounter();
	}

  /*************************************************************************************************
  */
  public long getNbDeplacements() {
    return mNbDeplacements;
  }
  /****************************************************************************/

  /*************************************************************************************************
  */
  public int getX() {
    return x;
  }

  /*************************************************************************************************
  */
  public int getY() {
    return y;
  }

  /*************************************************************************************************
   * Titre : modulo Description : Fcontion de modulo permettant au fourmi de
   * reapparaitre de l autre coté du Canvas lorsque qu'elle sorte de ce dernier
   *
   * @param x
   *          valeur
   *
   * @return int
   */
  private int modulo(int x, int m) {
    return (x + m) % m;
  }

  /*************************************************************************************************
   * Titre : boolean testCouleur() Description : fonction testant l'égalité
   * d'une couleur avec la couleur suivie
   *
   */
  private boolean testCouleur(int pCouleur) {
	float lLuminance;
	/* on calcule la luminance */
	lLuminance = 0.2426f * ((pCouleur >> 16) & 0x000000FF) + 0.7152f * ((pCouleur >> 8) & 0x000000FF) + 0.0722f * (pCouleur & 0x000000FF);

	return (Math.abs(mLuminanceCouleurSuivie - lLuminance) < mSeuilLuminance);
}
}
