import { Concrete } from './util.type';
import { components } from '@/schema';

export type GetSuggestionRankingPicksResponseType = {
  dailyViewRanking: Concrete<
    components['schemas']['techpick.api.application.ranking.dto.LinkInfoWithCount']
  >[];
  weeklyViewRanking: Concrete<
    components['schemas']['techpick.api.application.ranking.dto.LinkInfoWithCount']
  >[];
  monthlyPickRanking: Concrete<
    components['schemas']['techpick.api.application.ranking.dto.LinkInfoWithCount']
  >[];
};
