import math
def PixelDistance():
    r1 = input("Red: ")
    g1 = input("Green: ")
    b1 = input("Blue: ")
    # r2 = input("Red 2: ")
    # g2 = input("Green 2: ")
    # b2 = input("Blue 2: ")
    r2 = 0
    g2 = 0
    b2 = 0
    rgb = math.sqrt(math.pow(float(r1)-float(r2), 2) + math.pow(float(g1)-float(g2), 2) + math.pow(float(b1)-float(b2), 2))
    print("PixelDistance: " + str(rgb))

con = True
while con:
    PixelDistance()
    cont = input("Continue? (y/n)")
    if cont == "n" or cont == "N":
        con = False