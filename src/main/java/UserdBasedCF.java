import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.LongPrimitiveIterator;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.EuclideanDistanceSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.UncenteredCosineSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * 基于用户的 商品推荐
 * 1）分析各个用户（user） 对物品（item）的评分
 * 2）根据用户对物品的评分，计算得到所有用户之间的相似度（余弦相似度，欧式距离等等）
 * 3）选出 与 当前用户 最相似的N个用户
 * 4）将当前用户购买过的商品，推荐给其他用户；反之也行
 */
public class UserdBasedCF {
    public static void main(String[] args) throws IOException, TasteException {

        //根据数据源建立数据模型，就是： 打分矩阵
        String file = "D:\\temp\\data\\ratingdata.txt";
        DataModel model = new FileDataModel(new File(file));

        //根据打分矩阵，计算用户的相似度（余弦相似度，欧式距离等等）
//        UserSimilarity similarity = new UncenteredCosineSimilarity(model); //计算余弦相似度
        UserSimilarity similarity = new EuclideanDistanceSimilarity(model);   //欧式距离相似度

        //找到与用户相邻的用户，即：最相似的用户
        NearestNUserNeighborhood nearestNUserNeighborhood = new NearestNUserNeighborhood(2, similarity, model);

        //构建基于用户的推荐引擎
        Recommender r = new GenericUserBasedRecommender(model, nearestNUserNeighborhood, similarity);

        //进行推荐：
        System.out.println("***************给一个用户推荐商品***********");

        //r.recommend(userID, howMany)  userID 用户ID，howMany 最多推荐几个商品
        List<RecommendedItem> recommend = r.recommend(3, 2);
        for (RecommendedItem item : recommend) {
            System.out.println("给该用户推荐的商品是： " + item.getItemID() + " 推荐的理由：" + item.getValue());
        }

        System.out.println("");
        System.out.println("");
        System.out.println("************* 给每个用户推荐商品*****************");

        //首先得到每个用户的ID
        LongPrimitiveIterator userIDs = model.getUserIDs();
        while (userIDs.hasNext()) {
            long userID = userIDs.nextLong();

            List<RecommendedItem> list = r.recommend(userID, 2);
            System.out.println("用户ID: " + userID);
            for (RecommendedItem item : list) {
                System.out.println("给改用户推荐的商品是： " + item.getItemID() + " 推荐的理由：" + item.getValue());
            }
        }
    }
}
