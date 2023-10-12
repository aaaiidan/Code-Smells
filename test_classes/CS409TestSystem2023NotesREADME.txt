Details of Code Smells in CS409 Test System
Your solution should process this entire syste. 
However, don't be overly reliant on it - your solution should also be able to handle any other classes thrown at it.

Bloaters
- Grid is Large class and contains long methods (draw and playerMove), and long parameter lists (checkLine)! 
- Line and Choice (within Grid) could also be considered examples of data classes

Abusers
- Temporary field - BarnsleyFernTwo
- Refused bequest - two cases, one in ChqAcc and the other in SavingsAcc

Dispensibles
- Data Classes in several other instances of other examples (Grid in Bloaters, Item and Phone in the FeatureEnvy package, and Parent in the MessageChains package)

Couplers
- Feature Envy - FeatureEnvy Customer and Phone, and Item and Basket
  (and Item and Phone are also Data Classes)
- Inappropriate Intimacy - Huffman code accesses the files of HuffmanLeaf, HuffmanNode and HuffmanTree (not a particularly strong example)
- Middle Man - AccountManager
- Message Chains - Client (and Parent could be flagged up as a Data Class) and another example in Cipolla (Point and Triple could also be considered data classes_







